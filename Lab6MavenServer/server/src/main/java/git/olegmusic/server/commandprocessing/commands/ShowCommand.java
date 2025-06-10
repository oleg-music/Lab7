package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ShowCommand implements Command {
    @Override
    public void setArgument(String argument) {
    }

    @Override
    public void setPerson(Person person) {
    }

    @Override
    public void setUserLogin(String login) {
    }

    @Override
    public String execute() {
        List<Person> sorted = CollectionManager.getPersonSet()
                .stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        if (sorted.isEmpty()) {
            return "Коллекция пуста.";
        }

        StringBuilder sb = new StringBuilder("Содержимое коллекции:\n");
        for (Person p : sorted) {
            sb.append(p).append("\n");
        }

        return sb.toString().strip();
    }

    @Override
    public String descr() {
        return "show : вывести в стандартный вывод все элементы коллекции";
    }
}
