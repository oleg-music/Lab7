package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Команда clear: удаляет из коллекции (и из БД) все элементы,
 * принадлежащие текущему пользователю (owner).
 */
public class ClearCommand implements Command {
    private String userLogin;

    @Override
    public void setArgument(String argument) {
    }

    @Override
    public void setPerson(Person person) {
    }

    @Override
    public void setUserLogin(String login) {
        this.userLogin = login;
    }

    @Override
    public String execute() {
        if (userLogin == null) {
            return "Ошибка: пользователь не авторизован.";
        }

        List<Person> toRemove = new ArrayList<>();
        for (Person p : CollectionManager.getPersonSet()) {
            if (userLogin.equals(p.getOwner())) {
                toRemove.add(p);
            }
        }

        if (toRemove.isEmpty()) {
            return "У вас нет элементов для удаления.";
        }

        int removedCount = 0;
        for (Person p : toRemove) {
            if (CollectionManager.removePerson(p)) {
                removedCount++;
            }
        }

        return "Удалено ваших элементов: " + removedCount;
    }

    @Override
    public String descr() {
        return "clear : удалить из коллекции все элементы, принадлежащие текущему пользователю";
    }
}
