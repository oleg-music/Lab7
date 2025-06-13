package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.core.Invoker;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;
import git.olegmusic.server.commandprocessing.utils.Reader;
import git.olegmusic.server.commandprocessing.utils.ScriptManager;

import java.util.ArrayList;

public class ExecuteScriptCommand implements Command {
    private String argument;
    private Person person;
    private final Invoker invoker;
    private String userLogin;

    private static ArrayList<String> remainingScriptStrings = null;

    public ExecuteScriptCommand(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public void setUserLogin(String login) {                     // <<<— ДОБАВИТЬ ЭТО
        this.userLogin = login;                                   // <<<— ДОБАВИТЬ ЭТО
    }

    @Override
    public void setArgument(String argument) {
        this.argument = argument;
    }

    @Override
    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String execute() {
        if (argument == null || argument.trim().isEmpty()) {
            return "Ошибка: не указано имя файла скрипта.";
        }
        String fileName = argument.trim();

        if (ScriptManager.isScriptExecuting(fileName)) {
            return "Обнаружена рекурсия: скрипт '" + fileName + "' уже выполняется.";
        }

        ScriptManager.addExecutingScript(fileName);
        ArrayList<String> scriptLines = Reader.readFileInStrings(fileName);

        if (scriptLines == null || scriptLines.isEmpty()) {
            ScriptManager.removeExecutingScript(fileName);
            return "Ошибка: скрипт '" + fileName + "' пуст или не существует.";
        }

        StringBuilder output = new StringBuilder();
        int i = 0;
        while (i < scriptLines.size()) {
            String line = scriptLines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) {
                i++;
                continue;
            }

            ArrayList<String> remainingLines = new ArrayList<>();
            for (int j = i + 1; j < scriptLines.size(); j++) {
                remainingLines.add(scriptLines.get(j));
            }
            setRemainingScriptStrings(remainingLines);

            int before = remainingLines.size();

            String[] parts = line.split("\\s+", 2);
            String commandName = parts[0];
            String cmdArg = parts.length > 1 ? parts[1] : null;

            String result = invoker.processScriptLine(commandName, cmdArg, remainingLines);
            output.append(result).append("\n");

            int after = getRemainingScriptStrings() != null ? getRemainingScriptStrings().size() : 0;
            int used = before - after;

            i = i + 1 + used;
            setRemainingScriptStrings(null);
        }

        ScriptManager.removeExecutingScript(fileName);
        for (Person p : new ArrayList<>(CollectionManager.getPersonSet())) {
            if (p.getOwner() == null) {
                p.setOwner(userLogin);
                // Обновляем запись в БД и в кэше
                CollectionManager.updatePerson(p);
            }
        }
        return output.toString().strip();
    }


    @Override
    public String descr() {
        return "execute_script file_name : считать и исполнить скрипт из указанного файла.";
    }

    public static ArrayList<String> getRemainingScriptStrings() {
        return remainingScriptStrings;
    }

    public static void setRemainingScriptStrings(ArrayList<String> lines) {
        remainingScriptStrings = lines;
    }
}
