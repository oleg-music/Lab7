package git.olegmusic.client.meta;

public class ClientCommandInfo {
    private final boolean requiresPerson;

    public ClientCommandInfo(boolean requiresPerson) {
        this.requiresPerson = requiresPerson;
    }

    public boolean requiresPerson() {
        return requiresPerson;
    }
}
