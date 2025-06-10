package git.olegmusic.common;

import git.olegmusic.common.Person;

import java.io.Serializable;

public class CommandRequest implements Serializable {
    private String commandName;
    private String argument;
    private Person personData;

    public CommandRequest(String commandName, String argument, Person personData) {
        this.commandName = commandName;
        this.argument = argument;
        this.personData = personData;
    }

    public String getCommandName() { return commandName; }
    public String getArgument() { return argument; }
    public Person getPersonData() { return personData; }
}
