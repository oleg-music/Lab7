package git.olegmusic.server.commandprocessing.utils;

import java.util.HashSet;
import java.util.Set;

public class ScriptManager {
    private static final Set<String> executingScripts = new HashSet<>();

    public static boolean isScriptExecuting(String fileName) {
        return executingScripts.contains(fileName);
    }

    public static void addExecutingScript(String fileName) {
        executingScripts.add(fileName);
    }

    public static void removeExecutingScript(String fileName) {
        executingScripts.remove(fileName);
    }
}

