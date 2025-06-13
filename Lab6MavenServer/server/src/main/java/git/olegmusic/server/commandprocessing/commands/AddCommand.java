package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.PersonCreationServer;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

public class AddCommand implements Command {
    private String argument;
    private Person person;
    private String userLogin;

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
        personObj.setOwner(userLogin);
        // новый код: вставляем в БД и локальный кэш через CollectionManager
        // insert внутри DAO установит personObj.id в значение из БД
        int newId = CollectionManager.add(personObj);
        if (newId == -1) {
            return "Ошибка при добавлении элемента в БД.";
        }
        return "Элемент успешно добавлен!";
    }

    @Override
    public String descr() {
        return "add {element} : добавить новый элемент в коллекцию";
    }
}
