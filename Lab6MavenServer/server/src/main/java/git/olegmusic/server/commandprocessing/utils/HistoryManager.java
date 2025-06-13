package git.olegmusic.server.commandprocessing.utils;

import java.util.*;

/**
 * Хранит до 10 последних команд отдельно для каждого пользователя.
 */
public class HistoryManager {
    // Для каждого login — своя очередь последних 10 команд
    private static final Map<String, Deque<String>> historyMap = new HashMap<>();

    /**
     * Записать команду в историю пользователя.
     */
    public static void add(String userLogin, String cmd) {
        Deque<String> deque = historyMap.computeIfAbsent(userLogin, k -> new ArrayDeque<>());
        if (deque.size() == 10) {
            deque.removeFirst();
        }
        deque.addLast(cmd);
    }

    /**
     * Получить историю (до 10 последних) для данного пользователя.
     */
    public static List<String> getHistory(String userLogin) {
        Deque<String> deque = historyMap.getOrDefault(userLogin, new ArrayDeque<>());
        return Collections.unmodifiableList(new ArrayList<>(deque));
    }
}
