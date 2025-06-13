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
    /** Логин текущего пользователя, устанавливается сервером. */
    private String userLogin;

    @Override
    public void setArgument(String argument) {
        // нет аргументов
    }

    @Override
    public void setPerson(Person person) {
        // не используется
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

        // Собираем всех "своих" в отдельный список,
        // чтобы не модифицировать коллекцию во время итерации:
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
