package git.olegmusic.server.dao;

import git.olegmusic.common.Coordinates;
import git.olegmusic.common.EyeColor;
import git.olegmusic.common.HairColor;
import git.olegmusic.common.Location;
import git.olegmusic.common.Person;

import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public class TestPersonDAO {
    public static void main(String[] args) {
        try {
            PersonDAO dao = new PersonDAO();

            // 0) Убедитесь, что в users уже есть хотя бы тот логин, который вы будете использовать ниже.
            // Например, в предыдущем тесте "testLogin" уже зарегистрирован.
            String owner = "testLogin";

            // 1) Создаём нового Person
            Person p = new Person(
                    "Ivan Petrov",                      // name
                    new Coordinates(123L, 45.6f),       // coordinates (Long, Float)
                    180L,                               // height (Long)
                    ZonedDateTime.now(ZoneId.systemDefault()), // birthday (ZonedDateTime)
                    EyeColor.GREEN,                    // eyeColor
                    HairColor.BLACK,                   // hairColor
                    new Location(1.5f, 10, 2.7f, "Home")// location (float, Integer, float, String)
            );
            p.setCreationDate(new Date());       // задаём creationDate (Date)
            p.setOwner(owner);                   // задаём владельца

            // 2) Вставляем в БД
            int newId = dao.insert(p);
            System.out.println("Вставили Person, newId = " + newId);

            // 3) Читаем обратно по id
            Person loaded = dao.findById(newId);
            System.out.println("Загружен из БД: " + loaded);

            // 4) Меняем поля и делаем update
            loaded.setName("Иван Петрович");
            loaded.setLocation(new Location(9.9f, 20, 3.3f, "Office"));
            boolean updated = dao.update(loaded);
            System.out.println("Обновление вернуло: " + updated);

            // 5) Читаем всё (findAll) и проверяем, что изменилось
            List<Person> all = dao.findAll();
            System.out.println("Все люди в БД сейчас:");
            for (Person each : all) {
                System.out.println(each);
            }

            // 6) Удаляем вставленный тестовый объект
            boolean deleted = dao.delete(newId);
            System.out.println("Удаление вернуло: " + deleted);

            // 7) Ещё раз читаем всё (findAll) — список должен быть пустым
            List<Person> afterDelete = dao.findAll();
            System.out.println("После удаления осталось записей: " + afterDelete.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
