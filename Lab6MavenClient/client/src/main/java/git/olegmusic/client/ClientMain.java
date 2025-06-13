package git.olegmusic.client;

import git.olegmusic.client.meta.ClientCommandInfo;
import git.olegmusic.client.util.PersonCreation;
import git.olegmusic.common.CommandRequest;
import git.olegmusic.common.CommandResponse;
import git.olegmusic.common.Person;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClientMain {
    private static final String HOST = "localhost";
    private static final int PORT = 4000;
    private static final int MAX_RETRIES = 5;
    private static final int RETRY_DELAY_MS = 4000;

    public static void main(String[] args) {
        Map<String, ClientCommandInfo> commandInfo = new HashMap<>();
        commandInfo.put("add", new ClientCommandInfo(true));
        commandInfo.put("show", new ClientCommandInfo(false));
        commandInfo.put("info", new ClientCommandInfo(false));
        commandInfo.put("help", new ClientCommandInfo(false));
        commandInfo.put("clear", new ClientCommandInfo(false));
        commandInfo.put("update", new ClientCommandInfo(true));
        commandInfo.put("remove_by_id", new ClientCommandInfo(false));
        commandInfo.put("remove_greater", new ClientCommandInfo(true));
        commandInfo.put("remove_lower", new ClientCommandInfo(true));
        commandInfo.put("count_greater_than_birthday", new ClientCommandInfo(false));
        commandInfo.put("print_field_descending_birthday",
                new ClientCommandInfo(false));
        commandInfo.put("print_unique_eye_color", new ClientCommandInfo(false));
        commandInfo.put("history", new ClientCommandInfo(false));
        commandInfo.put("reg", new ClientCommandInfo(false));
        commandInfo.put("log", new ClientCommandInfo(false));
        commandInfo.put("execute_script", new ClientCommandInfo(false));


        Socket socket = connectWithRetries();
        if (socket == null) return;

        DataOutputStream dataOut = null;
        DataInputStream dataIn = null;
        Scanner scanner = new Scanner(System.in);
        try {
            dataOut = new DataOutputStream(socket.getOutputStream());
            dataIn = new DataInputStream(socket.getInputStream());

            while (true) {
                System.out.print("> ");
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                if ("exit".equalsIgnoreCase(line)) {
                    System.out.println("Exiting client.");
                    break;
                }

                String[] parts = line.split("\\s+", 2);
                String cmdName = parts[0].toLowerCase();
                ClientCommandInfo info = commandInfo.get(cmdName);
                if (info == null) {
                    System.out.println("Unknown command: " + cmdName);
                    continue;
                }

                String arg = null;
                Person person = null;
                if (info.requiresPerson()) {
                    if (cmdName.equals("update")) {
                        if (parts.length > 1) {
                            arg = parts[1];                     // вытащили id
                        }
                        person = PersonCreation.createPerson();  // и собрали новую персону
                    } else {
                        // остальные команды, которые требуют только Person
                        person = PersonCreation.createPerson();
                    }
                } else if (parts.length > 1) {
                    arg = parts[1];
                }

                byte[] reqBytes;
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                     ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                    oos.writeObject(new CommandRequest(cmdName, arg, person));
                    oos.flush();
                    reqBytes = bos.toByteArray();
                }

                try {
                    dataOut.writeInt(reqBytes.length);
                    dataOut.write(reqBytes);
                    dataOut.flush();

                    int respLen = dataIn.readInt();
                    byte[] respBytes = new byte[respLen];
                    dataIn.readFully(respBytes);

                    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(respBytes))) {
                        CommandResponse resp = (CommandResponse) ois.readObject();
                        System.out.println(resp.getMessage());
                    }
                } catch (IOException e) {
                    System.err.println("Connection lost: " + e.getMessage());

                    closeQuietly(dataOut);
                    closeQuietly(dataIn);
                    socket = connectWithRetries();
                    if (socket == null) break;
                    dataOut = new DataOutputStream(socket.getOutputStream());
                    dataIn = new DataInputStream(socket.getInputStream());
                    System.out.println("Connection restored. Пожалуйста, повторите команду.");
                } catch (ClassNotFoundException e) {
                    System.err.println("Deserialization error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Fatal IO error: " + e.getMessage());
        } finally {
            closeQuietly(dataOut);
            closeQuietly(dataIn);
            scanner.close();
            closeQuietly(socket);
        }
    }

    private static Socket connectWithRetries() {
        for (int i = 1; i <= MAX_RETRIES; i++) {
            try {
                Socket s = new Socket(HOST, PORT);
                System.out.println("Connected to server at " + HOST + ":" + PORT);
                return s;
            } catch (IOException e) {
                System.err.println("Attempt " + i + " failed: " + e.getMessage());
                if (i == MAX_RETRIES) {
                    System.err.println("Could not connect after " + MAX_RETRIES + " attempts. Exiting.");
                    return null;
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
        return null;
    }

    private static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static void closeQuietly(Socket s) {
        if (s != null) {
            try {
                s.close();
            } catch (IOException ignored) {
            }
        }
    }
}
