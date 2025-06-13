package git.olegmusic.server.dao;

import git.olegmusic.server.db.ConnectionFactory;
import git.olegmusic.server.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO для работы с таблицей users: регистрация и аутентификация.
 */
public class UserDAO {

    /**
     * Регистрирует нового пользователя.
     * @param user объект User с полями login и passwordHash
     * @return true, если вставка прошла успешно; false, если login уже существует
     * @throws SQLException при ошибках БД
     */
    public boolean register(User user) throws SQLException {
        String sql = "INSERT INTO users (login, password) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswordHash());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            // SQLState 23505 – нарушение уникальности (duplicate key)
            if ("23505".equals(ex.getSQLState())) {
                return false;
            } else {
                throw ex;
            }
        }
    }

    /**
     * Проверяет, существует ли запись с таким login и таким passwordHash.
     * @param login логин
     * @param passwordHash SHA-256-хеш пароля
     * @return true, если запись найдена, иначе false
     * @throws SQLException при ошибках БД
     */
    public boolean authenticate(String login, String passwordHash) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE login = ? AND password = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 1;
                }
                return false;
            }
        }
    }


    /**
     * При необходимости возвращает объект User по логину.
     * @param login логин
     * @return User или null, если не найден
     * @throws SQLException при ошибках БД
     */
    public User findByLogin(String login) throws SQLException {
        String sql = "SELECT login, password FROM users WHERE login = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String pwdHash = rs.getString("password");
                    return new User(login, pwdHash);
                }
                return null;
            }
        }
    }
}

