package git.olegmusic.server.dao;

import git.olegmusic.common.Coordinates;
import git.olegmusic.common.EyeColor;
import git.olegmusic.common.HairColor;
import git.olegmusic.common.Location;
import git.olegmusic.common.Person;
import git.olegmusic.server.db.ConnectionFactory;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DAO для работы с таблицей persons.
 *
 * Предполагаем, что таблица persons в PostgreSQL имеет следующие колонки:
 *
 *  id            SERIAL PRIMARY KEY,
 *  name          VARCHAR(128)      NOT NULL,
 *  x_coord       DOUBLE PRECISION  NOT NULL,
 *  y_coord       DOUBLE PRECISION  NOT NULL,
 *  creation_date TIMESTAMP         NOT NULL,
 *  height        BIGINT            NULL,
 *  birthday      TIMESTAMP         NOT NULL,
 *  eye_color     VARCHAR(20)       NULL,
 *  hair_color    VARCHAR(20)       NOT NULL,
 *  loc_x         DOUBLE PRECISION  NULL,
 *  loc_y         INTEGER           NULL,
 *  loc_z         DOUBLE PRECISION  NULL,
 *  loc_name      VARCHAR(128)      NULL,
 *  owner         VARCHAR(64)       NOT NULL REFERENCES users(login)
 */
public class PersonDAO {

    /**
     * Вставляет новый Person в таблицу и возвращает сгенерированный id.
     * Возвращает -1 в случае неудачи.
     */
    public int insert(Person p) throws SQLException {
        String sql = "INSERT INTO persons " +
                "(name, x_coord, y_coord, creation_date, height, birthday, " +
                " eye_color, hair_color, loc_x, loc_y, loc_z, loc_name, owner) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1) name (NOT NULL)
            if (p.getName() == null || p.getName().isBlank()) {
                throw new SQLException("Поле name не может быть NULL или пустым.");
            }
            ps.setString(1, p.getName());

            // 2) Coordinates: Long x, Float y → сохраняем как double/float (NOT NULL)
            if (p.getCoordinates() == null) {
                throw new SQLException("Поле coordinates не может быть NULL.");
            }
            Long coordX = p.getCoordinates().getX();
            Float coordY = p.getCoordinates().getY();
            ps.setDouble(2, coordX);
            ps.setDouble(3, coordY);

            // 3) creation_date: java.util.Date → java.sql.Timestamp (NOT NULL)
            if (p.getCreationDate() != null) {
                ps.setTimestamp(4, new Timestamp(p.getCreationDate().getTime()));
            } else {
                // Если creationDate не задан, ставим текущее время
                ps.setTimestamp(4, new Timestamp(new Date().getTime()));
            }

            // 4) height (Long) → BIGINT (NULLABLE)
            if (p.getHeight() != null) {
                ps.setLong(5, p.getHeight());
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            // 5) birthday: ZonedDateTime → java.sql.Timestamp (NOT NULL)
            ZonedDateTime bd = p.getBirthday();
            if (bd != null) {
                ps.setTimestamp(6, Timestamp.from(bd.toInstant()));
            } else {
                throw new SQLException("Поле birthday не может быть NULL.");
            }

            // 6) eye_color (enum.name()) (NULLABLE)
            if (p.getEyeColor() != null) {
                ps.setString(7, p.getEyeColor().name());
            } else {
                ps.setNull(7, Types.VARCHAR);
            }

            // 7) hair_color (enum.name()) (NOT NULL)
            if (p.getHairColor() != null) {
                ps.setString(8, p.getHairColor().name());
            } else {
                throw new SQLException("Поле hair_color не может быть NULL.");
            }

            // 8–11) Location: (float x, Integer y, Float z, String name) (NULLABLE)
            if (p.getLocation() != null) {
                float locX = p.getLocation().getX();
                Integer locY = p.getLocation().getY();
                float locZ = p.getLocation().getZ();
                String locName = p.getLocation().getName();

                ps.setFloat(9, locX);
                if (locY != null) {
                    ps.setInt(10, locY);
                } else {
                    ps.setNull(10, Types.INTEGER);
                }
                ps.setFloat(11, locZ);
                ps.setString(12, locName);
            } else {
                ps.setNull(9, Types.DOUBLE);
                ps.setNull(10, Types.INTEGER);
                ps.setNull(11, Types.DOUBLE);
                ps.setNull(12, Types.VARCHAR);
            }

            // 12) owner (String) (NOT NULL)
            if (p.getOwner() != null && !p.getOwner().isBlank()) {
                ps.setString(13, p.getOwner());
            } else {
                //throw new SQLException("Поле owner не может быть NULL или пустым.");
                ps.setNull(13, Types.VARCHAR);
            }

            // Выполняем INSERT и получаем сгенерированный id
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt("id");
                    p.setId(newId);
                    return newId;
                }
            }
            return -1;
        }
    }

    /**
     * Обновляет запись Person по id (все поля, кроме id).
     * Возвращает true, если хотя бы одна строка была обновлена.
     */
    public boolean update(Person p) throws SQLException {
        String sql = "UPDATE persons SET " +
                "name = ?, x_coord = ?, y_coord = ?, creation_date = ?, height = ?, " +
                "birthday = ?, eye_color = ?, hair_color = ?, loc_x = ?, loc_y = ?, loc_z = ?, loc_name = ?, owner = ? " +
                "WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1) name (NOT NULL)
            if (p.getName() == null || p.getName().isBlank()) {
                throw new SQLException("Поле name не может быть NULL или пустым.");
            }
            ps.setString(1, p.getName());

            // 2) coordinates (NOT NULL)
            if (p.getCoordinates() == null) {
                throw new SQLException("Поле coordinates не может быть NULL.");
            }
            ps.setDouble(2, p.getCoordinates().getX());
            ps.setDouble(3, p.getCoordinates().getY());

            // 3) creation_date (NOT NULL)
            if (p.getCreationDate() != null) {
                ps.setTimestamp(4, new Timestamp(p.getCreationDate().getTime()));
            } else {
                ps.setTimestamp(4, new Timestamp(new Date().getTime()));
            }

            // 4) height (BIGINT) (NULLABLE)
            if (p.getHeight() != null) {
                ps.setLong(5, p.getHeight());
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            // 5) birthday (NOT NULL)
            if (p.getBirthday() != null) {
                ps.setTimestamp(6, Timestamp.from(p.getBirthday().toInstant()));
            } else {
                throw new SQLException("Поле birthday не может быть NULL.");
            }

            // 6) eye_color (NULLABLE)
            if (p.getEyeColor() != null) {
                ps.setString(7, p.getEyeColor().name());
            } else {
                ps.setNull(7, Types.VARCHAR);
            }

            // 7) hair_color (NOT NULL)
            if (p.getHairColor() != null) {
                ps.setString(8, p.getHairColor().name());
            } else {
                throw new SQLException("Поле hair_color не может быть NULL.");
            }

            // 8–11) location (NULLABLE)
            if (p.getLocation() != null) {
                ps.setFloat(9, p.getLocation().getX());
                Integer locY = p.getLocation().getY();
                if (locY != null) {
                    ps.setInt(10, locY);
                } else {
                    ps.setNull(10, Types.INTEGER);
                }
                ps.setFloat(11, p.getLocation().getZ());
                ps.setString(12, p.getLocation().getName());
            } else {
                ps.setNull(9, Types.DOUBLE);
                ps.setNull(10, Types.INTEGER);
                ps.setNull(11, Types.DOUBLE);
                ps.setNull(12, Types.VARCHAR);
            }

            // 12) owner (NOT NULL)
            if (p.getOwner() != null && !p.getOwner().isBlank()) {
                ps.setString(13, p.getOwner());
            } else {
                //throw new SQLException("Поле owner не может быть NULL или пустым.");
                ps.setNull(13, Types.VARCHAR);
            }

            // 13) WHERE id = ?
            ps.setInt(14, p.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Удаляет Person по его id.
     * Возвращает true, если запись была успешно удалена.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM persons WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Возвращает объект Person из БД по заданному id, или null, если не найден.
     */
    public Person findById(int id) throws SQLException {
        String sql = "SELECT id, name, x_coord, y_coord, creation_date, height, birthday, " +
                "eye_color, hair_color, loc_x, loc_y, loc_z, loc_name, owner " +
                "FROM persons WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Person p = new Person();

                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));

                // Coordinates (Long, Float)
                Long coordX = rs.getObject("x_coord", Double.class).longValue();
                Float coordY = rs.getObject("y_coord", Double.class).floatValue();
                p.setCoordinates(new Coordinates(coordX, coordY));

                // creation_date
                Timestamp tsCreate = rs.getTimestamp("creation_date");
                if (tsCreate != null) {
                    p.setCreationDate(new Date(tsCreate.getTime()));
                }

                // height (Long) — nullable
                Long heightVal = rs.getObject("height", Long.class);
                p.setHeight(heightVal);

                // birthday (ZonedDateTime) — not null
                Timestamp tsBday = rs.getTimestamp("birthday");
                if (tsBday != null) {
                    p.setBirthday(ZonedDateTime.ofInstant(tsBday.toInstant(), ZoneId.systemDefault()));
                } else {
                    throw new SQLException("Поле birthday из БД оказалось NULL, хотя должно быть NOT NULL.");
                }

                // eyeColor — nullable
                String eyeStr = rs.getString("eye_color");
                if (eyeStr != null) {
                    p.setEyeColor(EyeColor.valueOf(eyeStr));
                } else {
                    p.setEyeColor(null);
                }

                // hairColor — not null
                String hairStr = rs.getString("hair_color");
                if (hairStr != null) {
                    p.setHairColor(HairColor.valueOf(hairStr));
                } else {
                    throw new SQLException("Поле hair_color из БД оказалось NULL, хотя должно быть NOT NULL.");
                }

                // location (loc_x, loc_y, loc_z, loc_name) — nullable
                Double locXd = rs.getObject("loc_x", Double.class);
                Integer locYi = rs.getObject("loc_y", Integer.class);
                Double locZd = rs.getObject("loc_z", Double.class);
                String locName = rs.getString("loc_name");
                if (locXd != null && locYi != null && locZd != null && locName != null) {
                    p.setLocation(new Location(locXd.floatValue(), locYi, locZd.floatValue(), locName));
                } else {
                    p.setLocation(null);
                }

                // owner (not null)
                String ownerStr = rs.getString("owner");
                if (ownerStr != null) {
                    p.setOwner(ownerStr);
                } else {
                    throw new SQLException("Поле owner из БД оказалось NULL, хотя должно быть NOT NULL.");
                }

                return p;
            }
        }
    }

    /**
     * Возвращает список всех Person из таблицы.
     */
    public List<Person> findAll() throws SQLException {
        String sql = "SELECT id, name, x_coord, y_coord, creation_date, height, birthday, " +
                "eye_color, hair_color, loc_x, loc_y, loc_z, loc_name, owner FROM persons";
        List<Person> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Person p = new Person();

                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));

                // Coordinates (Long, Float)
                Long coordX = rs.getObject("x_coord", Double.class).longValue();
                Float coordY = rs.getObject("y_coord", Double.class).floatValue();
                p.setCoordinates(new Coordinates(coordX, coordY));

                // creation_date
                Timestamp tsCreate = rs.getTimestamp("creation_date");
                if (tsCreate != null) {
                    p.setCreationDate(new Date(tsCreate.getTime()));
                }

                // height (Long) — nullable
                Long heightVal = rs.getObject("height", Long.class);
                p.setHeight(heightVal);

                // birthday (ZonedDateTime)
                Timestamp tsBday = rs.getTimestamp("birthday");
                if (tsBday != null) {
                    p.setBirthday(ZonedDateTime.ofInstant(tsBday.toInstant(), ZoneId.systemDefault()));
                }

                // eyeColor — nullable
                String eyeStr = rs.getString("eye_color");
                if (eyeStr != null) {
                    p.setEyeColor(EyeColor.valueOf(eyeStr));
                } else {
                    p.setEyeColor(null);
                }

                // hairColor
                String hairStr = rs.getString("hair_color");
                if (hairStr != null) {
                    p.setHairColor(HairColor.valueOf(hairStr));
                }

                // location
                Double locXd = rs.getObject("loc_x", Double.class);
                Integer locYi = rs.getObject("loc_y", Integer.class);
                Double locZd = rs.getObject("loc_z", Double.class);
                String locName = rs.getString("loc_name");
                if (locXd != null && locYi != null && locZd != null && locName != null) {
                    p.setLocation(new Location(locXd.floatValue(), locYi, locZd.floatValue(), locName));
                } else {
                    p.setLocation(null);
                }

                // owner
                p.setOwner(rs.getString("owner"));

                list.add(p);
            }
        }
        return list;
    }

    /**
     * Возвращает всех Person, чей owner совпадает с переданным логином.
     */
    public List<Person> findAllByOwner(String owner) throws SQLException {
        String sql = "SELECT id, name, x_coord, y_coord, creation_date, height, birthday, " +
                "eye_color, hair_color, loc_x, loc_y, loc_z, loc_name, owner " +
                "FROM persons WHERE owner = ?";
        List<Person> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, owner);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Person p = new Person();

                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));

                    Long coordX = rs.getObject("x_coord", Double.class).longValue();
                    Float coordY = rs.getObject("y_coord", Double.class).floatValue();
                    p.setCoordinates(new Coordinates(coordX, coordY));

                    Timestamp tsCreate = rs.getTimestamp("creation_date");
                    if (tsCreate != null) {
                        p.setCreationDate(new Date(tsCreate.getTime()));
                    }

                    Long heightVal = rs.getObject("height", Long.class);
                    p.setHeight(heightVal);

                    Timestamp tsBday = rs.getTimestamp("birthday");
                    if (tsBday != null) {
                        p.setBirthday(ZonedDateTime.ofInstant(tsBday.toInstant(), ZoneId.systemDefault()));
                    }

                    String eyeStr = rs.getString("eye_color");
                    if (eyeStr != null) {
                        p.setEyeColor(EyeColor.valueOf(eyeStr));
                    } else {
                        p.setEyeColor(null);
                    }

                    String hairStr = rs.getString("hair_color");
                    if (hairStr != null) {
                        p.setHairColor(HairColor.valueOf(hairStr));
                    }

                    Double locXd = rs.getObject("loc_x", Double.class);
                    Integer locYi = rs.getObject("loc_y", Integer.class);
                    Double locZd = rs.getObject("loc_z", Double.class);
                    String locName = rs.getString("loc_name");
                    if (locXd != null && locYi != null && locZd != null && locName != null) {
                        p.setLocation(new Location(locXd.floatValue(), locYi, locZd.floatValue(), locName));
                    } else {
                        p.setLocation(null);
                    }

                    p.setOwner(rs.getString("owner"));

                    list.add(p);
                }
            }
        }
        return list;
    }
}
