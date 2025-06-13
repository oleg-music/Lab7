package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Команда count_greater_than_birthday <date>: выводит количество элементов,
 * дата рождения которых позже указанной в формате dd.MM.yyyy
 */
public class CountGreaterThanBirthdayCommand implements Command {
    private String argument;

    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public void setArgument(String argument) {
        this.argument = argument;
    }


    @Override
    public void setPerson(Person person) {
        // не используется
    }

    @Override
    public void setUserLogin(String login) {
        // Эта команда не зависит от владельца, поэтому пустая реализация
    }

    @Override
    public String execute() {
        if (argument == null) {
            return "Ошибка: не указана дата для сравнения.";
        }
        ZonedDateTime targetDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate date = LocalDate.parse(argument, formatter);
            targetDate = date.atStartOfDay(ZoneId.systemDefault());
        } catch (Exception e) {
            return "Ошибка: неверный формат даты. Ожидается dd.MM.yyyy";
        }

        Set<Person> coll = CollectionManager.getPersonSet();

        long count = coll.stream()
                .filter(p -> p.getBirthday() != null && p.getBirthday().isAfter(targetDate))
                .count();

        return "Количество людей, у которых день рождения позже " + argument + ": " + count;
    }


    @Override
    public String descr() {
        return "count_greater_than_birthday <date> : вывести количество элементов, дата рождения которых больше указанной (формат dd.MM.yyyy)";
    }
}
