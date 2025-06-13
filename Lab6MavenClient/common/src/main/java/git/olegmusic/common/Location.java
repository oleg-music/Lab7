package git.olegmusic.common;

import java.io.Serializable;

/**
 * Класс Location представляет местоположение объекта.
 * Содержит координаты (x, y, z) и название местоположения.
 * Поля y и z не могут быть null, а поле name не может быть null или пустой строкой.
 */
public class Location implements Serializable {

    /**
     * Координата x. Не может быть null.
     */
    private float x;

    /**
     * Координата y. Не может быть null.
     */
    private Integer y;

    /**
     * Координата z. Не может быть null.
     */
    private Float z;

    /**
     * Название местоположения. Не может быть null или пустой строкой.
     */
    private String name;

    /**
     * Конструктор класса Location.
     *
     * @param x    Координата x. Не может быть null.
     * @param y    Координата y. Не может быть null.
     * @param z    Координата z. Не может быть null.
     * @param name Название местоположения. Не может быть null или пустой строкой.
     */
    public Location(float x, Integer y, Float z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    /**
     * Возвращает значение координаты x.
     *
     * @return Значение координаты x.
     */
    public float getX() {
        return x;
    }

    /**
     * Возвращает значение координаты z.
     *
     * @return Значение координаты z.
     */
    public Float getZ() {
        return z;
    }

    /**
     * Возвращает значение координаты y.
     *
     * @return Значение координаты y.
     */
    public Integer getY() {
        return y;
    }

    /**
     * Возвращает название местоположения.
     *
     * @return Название местоположения.
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает строковое представление объекта Location.
     *
     * @return Строковое представление объекта в формате "Location{x=..., y=..., z=..., name='...'}".
     */
    @Override
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", name='" + name + '\'' +
                '}';
    }
}
