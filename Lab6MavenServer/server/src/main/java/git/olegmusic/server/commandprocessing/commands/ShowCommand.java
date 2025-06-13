package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ShowCommand implements Command {
    @Override
    public void setArgument(String argument) {
        // у команды show нет аргументов
    }

    @Override
    public void setPerson(Person person) {
        // у команды show нет объекта Person в качестве входа
    }

    @Override
    public void setUserLogin(String login) {
        // ShowCommand не учитывает «владельца», так что пустая реализация
    }

    @Override
    public String execute() {
        // 1) Берём все Person из кеша, который был загружен из БД при старте
        List<Person> sorted = CollectionManager.getPersonSet()
                .stream()
                // сортируем в «естественном» порядке (по методу compareTo, который сравнивает по height)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        if (sorted.isEmpty()) {
            return "Коллекция пуста.";
        }

        // 2) Формируем итоговый StringBuilder
        StringBuilder sb = new StringBuilder("Содержимое коллекции:\n");
        for (Person p : sorted) {
            sb.append(p).append("\n");
        }
        // убираем лишний перевод строки в конце
        return sb.toString().strip();
    }

    @Override
    public String descr() {
        return "show : вывести в стандартный вывод все элементы коллекции";
    }
}
