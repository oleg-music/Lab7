package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Команда remove_greater {element} — удаляет из коллекции и БД все элементы,
 * которые больше заданного и принадлежат текущему пользователю.
 */
public class RemoveGreaterCommand implements Command {
    private Person person;
    private String userLogin;

    @Override
    public void setArgument(String argument) {
        // аргументом сюда не передаётся объект Person, используется setPerson
    }

    @Override
    public void setPerson(Person person) {
        this.person = person;
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
        if (person == null) {
            return "Ошибка: объект для сравнения не передан.";
        }

        Set<Person> all = CollectionManager.getPersonSet();
        List<Person> toRemove = new ArrayList<>();
        for (Person p : all) {
            if (p.compareTo(person) > 0 && userLogin.equals(p.getOwner())) {
                toRemove.add(p);
            }
        }
        if (toRemove.isEmpty()) {
            return "Нет ваших элементов, больших заданного.";
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
        return "remove_greater {element} : удалить из коллекции все элементы, большие заданного, принадлежащие текущему пользователю";
    }
}