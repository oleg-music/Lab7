package git.olegmusic.server.dao;

import git.olegmusic.server.model.User;

import java.sql.SQLException;

public class TestUserDAO {
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();

        try {
            // 1) Пробуем зарегистрировать нового пользователя
            User u = new User("testLogin", "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");
            // Здесь пароль должен быть SHA-256-хеш (64 hex-символа). Для теста можно взять любую строку из 64 символов.
            boolean registered = dao.register(u);
            System.out.println("Регистрация testLogin: " + registered);

            // 2) Пробуем зарегистрировать того же самого (должно быть false, потому что ключи уникальны)
            boolean duplicate = dao.register(u);
            System.out.println("Повторная регистрация того же login: " + duplicate);

            // 3) Пробуем аутентифицировать с правильным хешем
            boolean okAuth = dao.authenticate("testLogin", u.getPasswordHash());
            System.out.println("Аутентификация верна (ожидание true): " + okAuth);

            // 4) Пробуем аутентифицировать с неправильным хешем
            boolean badAuth = dao.authenticate("testLogin", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
            System.out.println("Аутентификация неверна (ожидание false): " + badAuth);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

