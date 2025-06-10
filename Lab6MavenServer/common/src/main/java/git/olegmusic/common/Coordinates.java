package git.olegmusic.common;

import java.io.Serializable;

/**
 * Класс Coordinates представляет координаты объекта.
 * Содержит два поля: x (тип Long) и y (тип Float).
 * Поле x не может быть null, а поле y должно быть больше -679 и также не может быть null.
 */
public class Coordinates implements Serializable {

    /**
     * Координата x. Не может быть null.
     */
    private Long x;

    /**
     * Координата y. Не может быть null, значение должно быть больше -679.
     */
    private Float y;

    /**
     * Конструктор класса Coordinates.
     *
     * @param x Координата x. Не может быть null.
     * @param y Координата y. Не может быть null, значение должно быть больше -679.
     */
    public Coordinates(Long x, Float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Возвращает значение координаты x.
     *
     * @return Значение координаты x.
     */
    public Long getX() {
        return x;
    }

    /**
     * Возвращает значение координаты y.
     *
     * @return Значение координаты y.
     */
    public Float getY() {
        return y;
    }

    /**
     * Возвращает строковое представление объекта Coordinates.
     *
     * @return Строковое представление объекта в формате "Coordinates{x=..., y=...}".
     */
    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
