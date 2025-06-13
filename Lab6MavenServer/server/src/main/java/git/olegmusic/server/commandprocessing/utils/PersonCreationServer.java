package git.olegmusic.server.commandprocessing.utils;

import git.olegmusic.server.commandprocessing.commands.ExecuteScriptCommand;
import git.olegmusic.common.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class PersonCreationServer {
    public static Person createPersonFromScript() {

        ArrayList<String> remainingScriptStrings = ExecuteScriptCommand.getRemainingScriptStrings();
        if (remainingScriptStrings == null || remainingScriptStrings.size() < 7) {
            throw new IllegalArgumentException("Ошибка: Недостаточно данных для создания объекта Person.");
        }

        String name = null;
        String coordinates = null;
        String height = null;
        String birthday = null;
        String eyeColor = null;
        String hairColor = null;
        String location = null;


        try {
            name = remainingScriptStrings.get(0);
            coordinates = remainingScriptStrings.get(1);
            height = remainingScriptStrings.get(2);
            birthday = remainingScriptStrings.get(3);
            eyeColor = remainingScriptStrings.get(4);
            hairColor = remainingScriptStrings.get(5);
            location = remainingScriptStrings.get(6);


            // Проверки на пустые строки
            if (name.trim().isEmpty() || coordinates.trim().isEmpty() || birthday.trim().isEmpty() || hairColor.trim().isEmpty()) {
                throw new IllegalArgumentException("Ошибка: Обязательные поля не могут быть пустыми.");
            }

            // Координаты
            String[] coordParts = coordinates.split(" ");
            if (coordParts.length != 2) {
                throw new IllegalArgumentException("Ошибка: Неверный формат координат. Ожидается 'x y'.");
            }
            Long x = Long.valueOf(coordParts[0]);
            Float y = Float.valueOf(coordParts[1]);
            if (y <= -679) {
                throw new IllegalArgumentException("Ошибка: Значение координаты 'y' должно быть больше -679.");
            }
            Coordinates coords = new Coordinates(x, y);

            // Рост
            Long heightValue = null;
            if (!height.trim().isEmpty()) {
                heightValue = Long.valueOf(height);
                if (heightValue <= 0) {
                    throw new IllegalArgumentException("Ошибка: Рост должен быть больше 0.");
                }
            }

            // Дата рождения
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate localDate = LocalDate.parse(birthday, dateFormatter);
            ZonedDateTime birthdayDate = localDate.atStartOfDay(ZoneId.systemDefault());

            // Цвет глаз
            EyeColor eyeColorValue = null;
            if (!eyeColor.trim().isEmpty()) {
                try {
                    eyeColorValue = EyeColor.valueOf(eyeColor);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Ошибка: Некорректный цвет глаз.");
                }
            }

            // Цвет волос
            HairColor hairColorValue;
            try {
                hairColorValue = HairColor.valueOf(hairColor);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Ошибка: Некорректный цвет волос.");
            }

            Location loc = null;
            if (!location.trim().isEmpty()) {
                String[] locationParts = location.split(" ");
                if (locationParts.length != 4) {
                    throw new IllegalArgumentException("Ошибка: Неверный формат локации. Ожидается 'x y z name'.");
                }
                float locX = Float.parseFloat(locationParts[0]);
                Integer locY = Integer.valueOf(locationParts[1]);
                Float locZ = Float.valueOf(locationParts[2]);
                String locName = locationParts[3];
                loc = new Location(locX, locY, locZ, locName);
            }

            // Только если все проверки и парсинг прошли — удаляем строки из списка
            remainingScriptStrings.subList(0, 7).clear();
            ExecuteScriptCommand.setRemainingScriptStrings(remainingScriptStrings);

            return new Person(name, coords, heightValue, birthdayDate, eyeColorValue, hairColorValue, loc);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка: Неверный формат числа в данных.");
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ошибка: Неверный формат даты. Ожидается формат dd.MM.yyyy.");
        } catch (IllegalArgumentException e) {
            throw e; // Просто пробрасываем дальше
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка при создании объекта Person: " + e.getMessage());
        }
    }
}
