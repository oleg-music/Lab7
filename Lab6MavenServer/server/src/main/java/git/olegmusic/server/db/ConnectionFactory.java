// src/main/java/git/olegmusic/server/db/ConnectionFactory.java
package git.olegmusic.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Утилита для получения JDBC-соединения к базе PostgreSQL studs на хосте pg:5432.
 * Аутентификация происходит через механизм peer/ident (текущий Unix-пользователь).
 */
public class ConnectionFactory {
    // URL БД: pg:5432, база studs
    private static final String URL = "jdbc:postgresql://pg:5432/studs";

    static {
        try {
            // Подгружаем драйвер PostgreSQL
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC-драйвер PostgreSQL не найден!");
            e.printStackTrace();
        }
    }

    /**
     * Возвращает новое соединение к базе studs. Аутентификация peer: текущий Unix-пользователь.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
