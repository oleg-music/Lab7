package git.olegmusic.common;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

public class Person implements Comparable<Person>, Serializable {

    private Integer id;
    private String name;
    private Coordinates coordinates;
    private Date creationDate;
    private Long height;
    private ZonedDateTime birthday;
    private EyeColor eyeColor;
    private HairColor hairColor;
    private Location location;

    private String owner;

    private static Integer idCounter = 1;

    public Person(String name,
                  Coordinates coordinates,
                  Long height,
                  ZonedDateTime birthday,
                  EyeColor eyeColor,
                  HairColor hairColor,
                  Location location) {
        this.name = name;
        this.coordinates = coordinates;
        this.height = height;
        this.birthday = birthday;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.location = location;

        this.id = Person.getNextId();
        this.creationDate = new Date();
    }

    public Person() { }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getHeight() {
        return this.height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public ZonedDateTime getBirthday() {
        return this.birthday;
    }

    public void setBirthday(ZonedDateTime birthday) {
        this.birthday = birthday;
    }

    public EyeColor getEyeColor() {
        return this.eyeColor;
    }

    public void setEyeColor(EyeColor eyeColor) {
        this.eyeColor = eyeColor;
    }

    public HairColor getHairColor() {
        return this.hairColor;
    }

    public void setHairColor(HairColor hairColor) {
        this.hairColor = hairColor;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public static int getNextId() {
        return idCounter++;
    }

    public static void setIdCounter(Integer idCounter) {
        Person.idCounter = idCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person other = (Person) o;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(Person other) {
        if (this.height == null && other.height == null) return 0;
        if (this.height == null) return -1;
        if (other.height == null) return 1;
        return this.height.compareTo(other.height);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", height=" + height +
                ", birthday=" + birthday +
                ", eyeColor=" + eyeColor +
                ", hairColor=" + hairColor +
                ", location=" + location +
                ", owner='" + owner + '\'' +
                '}';
    }
}
