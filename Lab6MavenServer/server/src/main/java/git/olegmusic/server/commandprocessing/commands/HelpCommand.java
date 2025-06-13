package git.olegmusic.server.commandprocessing.commands;

import java.util.HashMap;

public class HelpCommand implements Command {
    private final HashMap<String, Command> commands;

    public HelpCommand(HashMap<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public void setArgument(String argument) {}

    @Override
    public void setPerson(git.olegmusic.common.Person person) {}

    @Override
    public void setUserLogin(String login) {
        // Эта команда не зависит от владельца, поэтому пустая реализация
    }

    @Override
    public String execute() {
        StringBuilder output = new StringBuilder("Список доступных команд:\n");
        for (Command command : commands.values()) {
            output.append(command.descr()).append("\n");
        }
        return output.toString().strip();
    }

    @Override
    public String descr() {
        return "help : вывести справку по доступным командам";
    }
}
