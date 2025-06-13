package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.server.dao.UserDAO;
import git.olegmusic.server.model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * Команда reg <login> <password>
 * Регистрирует нового пользователя.
 */
public class RegCommand implements Command {
    private String argument; // ожидаем "login password"

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
        // не используется: регистрация без сессии
    }

    @Override
    public String execute() {
        if (argument == null || argument.isBlank()) {
            return "Ошибка: нужно указать логин и пароль, пример: reg user1 pass123";
        }
        String[] parts = argument.split("\\s+");
        if (parts.length != 2) {
            return "Ошибка: формат reg <login> <password>";
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
            boolean ok = userDao.register(new User(login, passwordHash));
            return ok
                    ? "Пользователь '" + login + "' зарегистрирован."
                    : "Ошибка: логин '" + login + "' уже существует.";
        } catch (SQLException e) {
            return "Ошибка БД при регистрации: " + e.getMessage();
        }
    }

    @Override
    public String descr() {
        return "reg <login> <password> : зарегистрировать пользователя";
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
