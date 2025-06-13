package git.olegmusic.server.commandprocessing.core;

import git.olegmusic.common.CommandRequest;
import git.olegmusic.common.CommandResponse;
import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.commands.*;
import git.olegmusic.server.commandprocessing.utils.HistoryManager;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Invoker {
    private static final String DATA_FILE = "server/src/main/resources/startFile.txt";

    private static final HashMap<String, Command> commands = new HashMap<>();
    private String currentScriptUser;
     {
        commands.put("help", new HelpCommand(commands));
        commands.put("info", new InfoCommand());
        commands.put("show", new ShowCommand());
        commands.put("add", new AddCommand());
        commands.put("update", new UpdateCommand());
        commands.put("remove_by_id", new RemoveByIdCommand());
        commands.put("clear", new ClearCommand());
        commands.put("count_greater_than_birthday", new CountGreaterThanBirthdayCommand());
        commands.put("history", new HistoryCommand());
        commands.put("print_unique_eye_color", new PrintUniqueEyeColorCommand());
        commands.put("print_field_descending_birthday", new PrintFieldDescendingBirthdayCommand());
        commands.put("remove_greater", new RemoveGreaterCommand());
        commands.put("remove_lower", new RemoveLowerCommand());
        commands.put("execute_script", new ExecuteScriptCommand(this));
        commands.put("reg", new RegCommand());
        commands.put("log", new LogCommand());
    }

    public CommandResponse process(CommandRequest request) {
        String name = request.getCommandName();
        Command cmd = commands.get(name);
        if (cmd == null) {
            return new CommandResponse("Неизвестная команда: " + name);
        }
        try {
            cmd.setArgument(request.getArgument());
            cmd.setPerson(request.getPersonData());
            return new CommandResponse(cmd.execute());
        } catch (Exception e) {
            return new CommandResponse("Ошибка выполнения: " + e.getMessage());
        }
    }

    public String processScriptLine(String name, String argument, ArrayList<String> paramLines) {
        Command cmd = commands.get(name);
        if (cmd == null) return "Неизвестная команда: " + name;
        if (currentScriptUser != null) {
            cmd.setUserLogin(currentScriptUser);
        }
        cmd.setArgument(argument);
        cmd.setPerson(null);
        return cmd.execute();
    }

    public String executeScriptWithUser(String fileName, String userLogin) {
        // 1) Берём новый экземпляр ExecuteScriptCommand
        Command cmd = createCommandInstance("execute_script");
        // 2) Проставляем контекст
        cmd.setUserLogin(userLogin);
        cmd.setArgument(fileName);
        cmd.setPerson(null);
        // 3) Запускаем и сразу обрезаем лишние пустые строки
        return cmd.execute().strip();
    }

    public Command createCommandInstance(String name) {
        switch (name.toLowerCase()) {
            case "show":                         return new ShowCommand();
            case "add":                          return new AddCommand();
            case "help":                         return new HelpCommand(commands);                          // <<<— ДОБАВЛЕНО
            case "info":                         return new InfoCommand();                          // <<<— ДОБАВЛЕНО
            case "history":                      return new HistoryCommand();                       // <<<— ДОБАВЛЕНО
            case "print_field_descending_birthday":
                return new PrintFieldDescendingBirthdayCommand();  // <<<— ДОБАВЛЕНО
            case "print_unique_eye_color":      return new PrintUniqueEyeColorCommand();
            case "count_greater_than_birthday":
                return new CountGreaterThanBirthdayCommand();
            case "remove_greater":              return new RemoveGreaterCommand();
            case "remove_lower":                return new RemoveLowerCommand();
            case "update":                      return new UpdateCommand();
            case "remove_by_id":                return new RemoveByIdCommand();
            case "clear":                       return new ClearCommand();
            case "execute_script":               return new ExecuteScriptCommand(this);
            case "reg":                          return new RegCommand();
            case "log":                          return new LogCommand();
            default:                            return null;
        }
    }



}

