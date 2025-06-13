package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

public class InfoCommand implements Command {

    @Override
    public void setArgument(String argument) {
    }

    @Override
    public void setPerson(Person person) {
    }

    @Override
    public void setUserLogin(String login) {
        // Эта команда не зависит от владельца, поэтому пустая реализация
    }

    @Override
    public String execute() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип коллекции: ").append(CollectionManager.getPersonSet().getClass()).append("\n");
        sb.append("Время инициализации: ").append(CollectionManager.getInitializationTime()).append("\n");
        sb.append("Количество элементов: ").append(CollectionManager.getPersonSet().size());
        return sb.toString();
    }

    @Override
    public String descr() {
        return "info : вывести информацию о коллекции (тип, дата инициализации, количество элементов)";
    }
}