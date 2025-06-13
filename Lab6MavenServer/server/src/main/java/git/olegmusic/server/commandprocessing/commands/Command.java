package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;

public interface Command {
    void setArgument(String argument);
    void setPerson(Person person);
    String execute();
    String descr();
    void setUserLogin(String login);
}
