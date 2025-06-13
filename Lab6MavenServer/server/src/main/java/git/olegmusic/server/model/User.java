package git.olegmusic.server.model;

/**
 * Модель пользователя: хранит login и SHA-256-хеш пароля.
 */
public class User {
    private String login;        // PRIMARY KEY
    private String passwordHash; // SHA-256 hex (64 символа)

    public User() { }

    public User(String login, String passwordHash) {
        this.login = login;
        this.passwordHash = passwordHash;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}

