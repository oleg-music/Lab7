package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Команда print_field_descending_birthday:
 * выводит значения поля birthday всех элементов коллекции в порядке убывания.
 */
public class PrintFieldDescendingBirthdayCommand implements Command {
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
        Set<Person> coll = CollectionManager.getPersonSet();

        String result = coll.stream()
                .filter(p -> p.getBirthday() != null)
                .sorted((p1, p2) -> p2.getBirthday().compareTo(p1.getBirthday()))
                .map(p -> p.getBirthday().toString())
                .collect(Collectors.joining("\n"));

        if (result.isEmpty()) {
            return "Нет людей с датой рождения.";
        }
        return "Дни рождения по убыванию:\n" + result;
    }


    @Override
    public String descr() {
        return "print_field_descending_birthday : вывести значения поля birthday всех элементов в порядке убывания";
    }
}
