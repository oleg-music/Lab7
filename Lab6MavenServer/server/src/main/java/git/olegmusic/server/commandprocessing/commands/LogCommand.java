package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.server.dao.UserDAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * Команда log <login> <password>
 * Проверяет логин/пароль и возвращает "OK" или ошибку.
 */
public class LogCommand implements Command {
    private String argument; // "login password"

    @Override
    public void setArgument(String argument) {
        this.argument = argument;
    }

    @Override
    public void setPerson(git.olegmusic.common.Person person) {
        // не используется
    }

    @Override
    public void setUserLogin(String login) {
        // не используется: вход без сессии
    }

    @Override
    public String execute() {
        if (argument == null || argument.isBlank()) {
            return "Ошибка: нужно указать логин и пароль, пример: log user1 pass123";
        }
        String[] parts = argument.split("\\s+");
        if (parts.length != 2) {
            return "Ошибка: формат log <login> <password>";
        }
        String login = parts[0];
        String rawPassword = parts[1];

        String passwordHash;
        try {
            passwordHash = sha256(rawPassword);
        } catch (NoSuchAlgorithmException e) {
            return "Внутренняя ошибка при хешировании пароля.";
        }

        UserDAO userDao = new UserDAO();
        try {
            boolean ok = userDao.authenticate(login, passwordHash);
            return ok ? "OK" : "Ошибка: неверный логин или пароль.";
        } catch (SQLException e) {
            return "Ошибка БД при аутентификации: " + e.getMessage();
        }
    }

    @Override
    public String descr() {
        return "log <login> <password> : войти в систему";
    }

    private String sha256(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            String h = Integer.toHexString(0xff & b);
            if (h.length() == 1) hex.append('0');
            hex.append(h);
        }
        return hex.toString();
    }
}
