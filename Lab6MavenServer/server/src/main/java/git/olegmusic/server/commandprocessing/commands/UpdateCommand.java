package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;
import git.olegmusic.server.commandprocessing.utils.PersonCreationServer;

import java.util.Optional;
import java.util.Set;

/**
 * Команда 'update' — обновляет элемент коллекции по id.
 * Работает только с теми элементами, владельцем которых является текущий пользователь.
 */
public class UpdateCommand implements Command {

    private String argument;
    private Person person;
    private String userLogin;  // логин текущего пользователя

    @Override
    public void setArgument(String argument) {
        this.argument = argument;
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

        // Разбор id
        if (argument == null || argument.isBlank()) {
            return "Ошибка: не указан id для обновления!";
        }
        int id;
        try {
            id = Integer.parseInt(argument.trim());
        } catch (NumberFormatException e) {
            return "Ошибка: id должен быть числом!";
        }

        // Поиск существующего элемента
        Set<Person> coll = CollectionManager.getPersonSet();
        Optional<Person> opt = coll.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        if (!opt.isPresent()) {
            return "Ошибка: элемент с id=" + id + " не найден.";
        }
        Person existing = opt.get();
        if (!userLogin.equals(existing.getOwner())) {
            return "Ошибка: вы не можете обновлять элемент другого пользователя.";
        }

        // Создание нового объекта Person из скрипта или переданного
        Person personObj;
        if (ExecuteScriptCommand.getRemainingScriptStrings() != null) {
            try {
                personObj = PersonCreationServer.createPersonFromScript();
            } catch (IllegalArgumentException e) {
                return e.getMessage();
            }
            if (personObj == null) {
                return "Ошибка: Не удалось создать объект Person из скрипта.";
            }
        } else {
            if (person == null) {
                return "Ошибка: объект Person не передан.";
            }
            personObj = person;
        }

        // Устанавливаем id и владельца
        personObj.setId(id);
        personObj.setOwner(userLogin);

        // Обновляем через CollectionManager (DAO + локальный кэш)
        boolean updated = CollectionManager.updatePerson(personObj);
        return updated
                ? "Элемент id=" + id + " успешно обновлён!"
                : "Ошибка при обновлении элемента id=" + id + "!";
    }

    @Override
    public String descr() {
        return "update <id> {element} : обновить элемент с заданным id";
    }
}
