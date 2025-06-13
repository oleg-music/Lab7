// src/main/java/git/olegmusic/server/db/TestConnection.java
package git.olegmusic.server.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Тест для проверки, что ConnectionFactory может открыть соединение и выполнить простой запрос.
 */
public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            System.out.println("Успешно подключились к БД!");

            // Выполнить простой запрос SELECT 1
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {
                if (rs.next()) {
                    int one = rs.getInt(1);
                    System.out.println("Результат SELECT 1 = " + one);
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при подключении или выполнении запроса:");
            e.printStackTrace();
        }
    }
}

