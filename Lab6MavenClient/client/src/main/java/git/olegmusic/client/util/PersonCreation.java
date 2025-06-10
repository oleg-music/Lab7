package git.olegmusic.client.util;

import git.olegmusic.common.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class PersonCreation {
    private static final Scanner scanner = new Scanner(System.in);

    public static Person createPerson() {
        String name = askName();
        Coordinates coordinates = askCoordinates();
        Long height = askHeight();
        ZonedDateTime birthday = askBirthday();
        EyeColor eyeColor = askEyeColor();
        HairColor hairColor = askHairColor();
        Location location = askLocation();

        return new Person(name, coordinates, height, birthday, eyeColor, hairColor, location);
    }

    private static String askName() {
        String name;
        System.out.print("Введите имя: ");
        while (true) {
            name = scanner.nextLine();
            if (name.trim() != "") {
                return name;
            } else {
                System.out.print("Поле не может быть пустым, введите имя еще раз: ");
            }
        }
    }

    private static Coordinates askCoordinates() {
        System.out.print("Введите координаты в формате 'x y': ");

        while (true) {
            String stringCoordinates = scanner.nextLine();
            if (stringCoordinates.trim() != "") {
                try {
                    String[] enteredCoordinates = stringCoordinates.split(" ");
                    Long x = Long.valueOf(enteredCoordinates[0]);
                    Float y = Float.valueOf(enteredCoordinates[1]);
                    return new Coordinates(x, y);

                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.print("Неверный формат. Введите координаты в формате 'x y': ");
                } catch (NumberFormatException e) {
                    System.out.print("Неверный формат. Введите координаты в формате 'x y', где x и y - числа: ");
                }
            } else {
                System.out.print("Поле не может быть пустым. Введите координаты в формате 'x y': ");
            }
        }
    }

    private static Long askHeight() {
        System.out.print("Введите рост: ");

        while (true) {
            String stringHeight = scanner.nextLine();
            try {
                if (stringHeight == "") {
                    return null;
                } else {
                    Long height = Long.valueOf(stringHeight);
                    if (height > 0) {
                        return height;
                    } else {
                        System.out.print("Рост должен быть больше 0, введите рост еще раз: ");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.print("Неверный формат. Введите число: ");
            }
        }
    }

    private static ZonedDateTime askBirthday() {
        System.out.print("Введите дату рождения в формате dd.MM.yyyy (например, 01.01.2001): ");

        while (true) {
            String stringBirthday = scanner.nextLine();
            if (stringBirthday.trim() != "") {
                try {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate localDate = LocalDate.parse(stringBirthday, dateFormatter);
                    return localDate.atStartOfDay(ZoneId.systemDefault());
                } catch (DateTimeParseException e) {
                    System.out.print("Неверный формат. Введите дату рождения в формате dd.MM.yyyy " +
                            "(например, 01.01.2001): ");
                }
            } else {
                System.out.print("Поле не может быть пустым. Введите дату рождения еще раз: ");
            }
        }
    }

    private static EyeColor askEyeColor() {
        System.out.print("Введите цвет глаз, любой из: ");
        for (
                EyeColor value : EyeColor.values()) {
            System.out.print(value + " ");
        }

        while (true) {
            String stringEyeColor = scanner.nextLine();
            try {
                if (stringEyeColor == "") {
                    return null;
                } else {
                    return EyeColor.valueOf(stringEyeColor);
                }
            } catch (IllegalArgumentException e) {
                System.out.print("Такого цвета глаз не существует. Введите цвет глаз еще раз: ");
            }
        }
    }

    private static HairColor askHairColor() {
        System.out.print("Введите цвет волос, любой из: ");
        for (
                HairColor value : HairColor.values()) {
            System.out.print(value + " ");
        }

        while (true) {
            String stringHairColor = scanner.nextLine();
            if (stringHairColor.trim() != "") {
                try {
                    return HairColor.valueOf(stringHairColor);
                } catch (IllegalArgumentException e) {
                    System.out.print("Такого цвета волос не существует. Введите цвет волос еще раз: ");
                }
            } else {
                System.out.print("Поле не может быть пустым. Введите цвет волос еще раз: ");
            }
        }
    }

    private static Location askLocation() {
        System.out.print("Введите координаты и название локации в формате 'x y z nameOfLocation': ");

        while (true) {
            String stringLocation = scanner.nextLine();
            try {
                if (stringLocation == "") {
                    return null;
                } else {
                    String[] enteredLocation = stringLocation.split(" ");
                    float x = Float.parseFloat(enteredLocation[0]);
                    Integer y = Integer.valueOf(enteredLocation[1]);
                    Float z = Float.valueOf(enteredLocation[2]);
                    String nameOfLocation = enteredLocation[3];
                    return new Location(x, y, z, nameOfLocation);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.print("Неверный формат. Введите координаты и название локации в формате" +
                        " 'x y z nameOfLocation': ");
            } catch (NumberFormatException e) {
                System.out.print("Неверный формат. Введите координаты и название локации в формате" +
                        " 'x y z nameOfLocation', где x, y, z - числа, а nameOfLocation - строка: ");
            }
        }
    }
}
