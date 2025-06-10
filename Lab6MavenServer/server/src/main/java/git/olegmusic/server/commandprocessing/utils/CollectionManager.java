package git.olegmusic.server.commandprocessing.utils;

import git.olegmusic.common.Person;
import git.olegmusic.server.dao.PersonDAO;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * CollectionManager теперь «на лету» синхронизирует операции с таблицей persons.
 * Доступ к personSet защищён через ReentrantReadWriteLock.
 */
public class CollectionManager {

    private static Date initializationTime = new Date();

    private static final HashSet<Person> personSet = new HashSet<>();

    private static final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    static {
        loadFromDatabase();
    }

    private static void loadFromDatabase() {
        PersonDAO dao = new PersonDAO();
        try {
            List<Person> all = dao.findAll();
            rwLock.writeLock().lock();
            try {
                personSet.addAll(all);
            } finally {
                rwLock.writeLock().unlock();
            }
        } catch (SQLException e) {
            System.err.println("Ошибка загрузки коллекции из БД: " + e.getMessage());
        }
    }

    public static Date getInitializationTime() {
        return initializationTime;
    }

    public static Set<Person> getPersonSet() {
        rwLock.readLock().lock();
        try {
            return new HashSet<>(personSet);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public static int add(Person person) {
        rwLock.writeLock().lock();
        try {
            PersonDAO dao = new PersonDAO();
            int newId = dao.insert(person);
            if (newId != -1) {
                personSet.add(person);
            }
            return newId;
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении Person в БД: " + e.getMessage());
            return -1;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public static boolean removePerson(Person person) {
        rwLock.writeLock().lock();
        try {
            PersonDAO dao = new PersonDAO();
            boolean deleted = dao.delete(person.getId());
            if (deleted) {
                personSet.remove(person);
            }
            return deleted;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении Person из БД: " + e.getMessage());
            return false;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public static boolean updatePerson(Person person) {
        rwLock.writeLock().lock();
        try {
            PersonDAO dao = new PersonDAO();
            boolean updated = dao.update(person);
            if (updated) {
                personSet.remove(person);
                personSet.add(person);
            }
            return updated;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении Person в БД: " + e.getMessage());
            return false;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public static void clearPersonSet() {
        rwLock.writeLock().lock();
        try {
            PersonDAO dao = new PersonDAO();
            for (Person p : new ArrayList<>(personSet)) {
                dao.delete(p.getId());
            }
            personSet.clear();
        } catch (SQLException e) {
            System.err.println("Ошибка при очистке таблицы persons: " + e.getMessage());
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
