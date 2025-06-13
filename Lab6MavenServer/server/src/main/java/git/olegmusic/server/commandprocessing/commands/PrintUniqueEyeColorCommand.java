package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.common.EyeColor;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Команда print_unique_eye_color:
 * выводит уникальные значения поля eyeColor всех элементов коллекции.
 */
public class PrintUniqueEyeColorCommand implements Command {
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
                .map(Person::getEyeColor)
                .filter(Objects::nonNull)
                .distinct()
                .map(EyeColor::toString)
                .collect(Collectors.joining("\n"));

        if (result.isEmpty()) {
            return "Нет уникальных цветов глаз.";
        }
        return "Уникальные цвета глаз в коллекции:\n" + result;
    }


    @Override
    public String descr() {
        return "print_unique_eye_color : вывести уникальные значения поля eyeColor всех элементов коллекции";
    }
}
