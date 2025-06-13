package git.olegmusic.common;

import java.io.Serializable;

public class CommandResponse implements Serializable {
    private String message;

    public CommandResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
