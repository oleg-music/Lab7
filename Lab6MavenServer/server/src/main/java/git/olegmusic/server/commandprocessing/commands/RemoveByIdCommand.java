package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

import java.util.Optional;
import java.util.Set;

/**
 * Команда remove_by_id <id> — удаляет элемент коллекции и БД по его id,
 * но только если он принадлежит текущему пользователю.
 */
public class RemoveByIdCommand implements Command {
    private String argument;
    private String userLogin;

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
        this.userLogin = login;
    }

    @Override
    public String execute() {
        if (userLogin == null) {
            return "Ошибка: пользователь не авторизован.";
        }
        if (argument == null || argument.isBlank()) {
            return "Ошибка: не указан id для удаления!";
        }

        int id;
        try {
            id = Integer.parseInt(argument.trim());
        } catch (NumberFormatException e) {
            return "Ошибка: id должен быть числом!";
        }

        Set<Person> coll = CollectionManager.getPersonSet();
        Optional<Person> opt = coll.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (!opt.isPresent()) {
            return "Элемент с id " + id + " не найден.";
        }

        Person toRemove = opt.get();
        if (!userLogin.equals(toRemove.getOwner())) {
            return "Ошибка: вы не можете удалять элемент другого пользователя.";
        }

        boolean removed = CollectionManager.removePerson(toRemove);
        return removed
                ? "Элемент с id " + id + " успешно удалён."
                : "Ошибка при удалении элемента id=" + id + "!";
    }

    @Override
    public String descr() {
        return "remove_by_id <id> : удалить элемент коллекции по id (только свой)";
    }
}
