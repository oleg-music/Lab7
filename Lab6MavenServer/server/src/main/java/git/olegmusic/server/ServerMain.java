package git.olegmusic.server;

import git.olegmusic.common.CommandRequest;
import git.olegmusic.common.CommandResponse;
import git.olegmusic.server.commandprocessing.commands.Command;
import git.olegmusic.server.commandprocessing.core.Invoker;
import git.olegmusic.server.commandprocessing.utils.HistoryManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class ServerMain {
    private static final int PORT = 4000;

    private static final ExecutorService fixedPool =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static Selector selector;

    private static final Map<SocketChannel, ClientContext> contexts = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Invoker invoker = new Invoker();

        selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server listening on port " + PORT);

        while (true) {
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                if (key.isAcceptable()) {
                    doAccept(key);
                } else if (key.isReadable()) {
                    doRead(key, invoker);
                } else if (key.isWritable()) {
                    doWrite(key);
                }
            }
        }
    }

    private static void doAccept(SelectionKey key) throws IOException {
        ServerSocketChannel srv = (ServerSocketChannel) key.channel();
        SocketChannel client = srv.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        contexts.put(client, new ClientContext());
        System.out.println("Client connected: " + client.getRemoteAddress());
    }

    private static void doRead(SelectionKey key, Invoker invoker) {
        SocketChannel client = (SocketChannel) key.channel();
        ClientContext ctx = contexts.get(client);
        if (ctx == null) {
            closeClient(client, key);
            return;
        }

        final ByteBuffer lenBuf = ctx.lenBuffer;
        final ClientContext finalCtx = ctx;
        final SelectionKey selectionKey = key;

        new Thread(() -> {
            try {
                if (lenBuf.hasRemaining()) {
                    int r;
                    try {
                        r = client.read(lenBuf);
                    } catch (IOException | NullPointerException ex) {
                        ex.printStackTrace();
                        closeClient(client, selectionKey);
                        return;
                    }
                    if (r == -1) {
                        closeClient(client, selectionKey);
                        return;
                    }
                    if (lenBuf.hasRemaining()) {
                        return;
                    }
                    lenBuf.flip();
                    int msgLen = lenBuf.getInt();
                    finalCtx.msgBuffer = ByteBuffer.allocate(msgLen);
                }

                ByteBuffer msgBuf = finalCtx.msgBuffer;
                int r2;
                try {
                    r2 = client.read(msgBuf);
                } catch (IOException | NullPointerException ex) {
                    ex.printStackTrace();
                    closeClient(client, selectionKey);
                    return;
                }
                if (r2 == -1) {
                    closeClient(client, selectionKey);
                    return;
                }
                if (msgBuf.hasRemaining()) {
                    return;
                }

                lenBuf.clear();
                msgBuf.flip();

                CommandRequest request = deserialize(msgBuf);

                fixedPool.submit(() -> {
                    CommandResponse resp = processRequest(request, finalCtx, invoker);
                    ForkJoinPool.commonPool().execute(() -> {
                        try {
                            sendResponse(client, finalCtx, resp, selectionKey);
                        } catch (IOException e) {
                            e.printStackTrace();
                            closeClient(client, selectionKey);
                        }
                    });
                });

            } catch (Throwable t) {
                t.printStackTrace();
                closeClient(client, selectionKey);
            }
        }).start();
    }

    private static CommandResponse processRequest(CommandRequest request,
                                                  ClientContext ctx,
                                                  Invoker invoker) {
        String name = request.getCommandName().toLowerCase();
        Command cmd = invoker.createCommandInstance(name);

        if (cmd == null) {
            return new CommandResponse("Неизвестная команда: " + name);
        }

        if (name.equals("reg") || name.equals("log")) {
            CommandResponse res = invoker.process(request);
            if (name.equals("log") && "OK".equals(res.getMessage())) {
                String login = request.getArgument().split("\\s+")[0];
                ctx.currentUser = login;
            }
            return res;
        }

        if (ctx.currentUser == null) {
            return new CommandResponse("Not authorized. Please log in first.");
        }

        HistoryManager.add(ctx.currentUser, name);
        cmd.setUserLogin(ctx.currentUser);
        cmd.setArgument(request.getArgument());
        cmd.setPerson(request.getPersonData());
        return new CommandResponse(cmd.execute());
    }

    private static void sendResponse(SocketChannel client,
                                     ClientContext ctx,
                                     CommandResponse response,
                                     SelectionKey key) throws IOException {
        ByteBuffer out = serialize(response);
        synchronized (ctx.responses) {
            ctx.responses.add(out);
        }
        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        selector.wakeup();
    }

    private static void doWrite(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ClientContext ctx = contexts.get(client);
        synchronized (ctx.responses) {
            while (!ctx.responses.isEmpty()) {
                ByteBuffer buf = ctx.responses.peek();
                client.write(buf);
                if (buf.hasRemaining()) break;
                ctx.responses.poll();
            }
            if (ctx.responses.isEmpty()) {
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            }
        }
    }

    private static void closeClient(SocketChannel client, SelectionKey key) {
        try {
            contexts.remove(client);
            key.cancel();
            client.close();
        } catch (IOException ignored) {}
    }

    private static CommandRequest deserialize(ByteBuffer buf) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(buf.array());
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (CommandRequest) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    private static ByteBuffer serialize(CommandResponse resp) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(resp);
        }
        byte[] data = bos.toByteArray();
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES + data.length);
        buf.putInt(data.length).put(data).flip();
        return buf;
    }

    private static class ClientContext {
        ByteBuffer lenBuffer  = ByteBuffer.allocate(Integer.BYTES);
        ByteBuffer msgBuffer;
        final Queue<ByteBuffer> responses = new LinkedList<>();
        String currentUser = null;
    }
}
