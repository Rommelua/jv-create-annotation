package core.basesyntax.factory;

import core.basesyntax.dao.BetDao;
import core.basesyntax.dao.BetDaoImpl;
import core.basesyntax.dao.UserDao;
import core.basesyntax.dao.UserDaoImpl;
import core.basesyntax.exception.AnnotationAbsentException;
import core.basesyntax.lib.Dao;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

public class Factory {
    public static final String EXCEPTION_MESSAGE = " class doesnt have Dao annotation";
    public static final String DAO_PACKAGE = "core.basesyntax.dao.";
    public static final Path PATH_TO_DAO_CLASSES = Path.of("target/classes/core/basesyntax/dao");
    private static final ClassLoader classLoader = Factory.class.getClassLoader();

    private static BetDao betDao;
    private static UserDao userDao;

    public static BetDao getBetDao() {
        if (betDao == null) {
            initBetDao();
        }
        return betDao;
    }

    private static void initBetDao() {
        try {
            Class<?> betDaoClazz = Files.walk(PATH_TO_DAO_CLASSES)
                    .filter((e -> e.toString().endsWith(".class")))
                    .map(path -> DAO_PACKAGE + path.getFileName().toString()
                            .replace(".class", ""))
                    .map(Factory::loadClass)
                    .filter(clazz -> clazz.isAnnotationPresent(Dao.class)
                                     && clazz.getInterfaces()[0].getName()
                                             .equals(BetDao.class.getName()))
                    .findFirst()
                    .orElseThrow(() -> new AnnotationAbsentException(
                            "Can't find implementation for BetDao"));
            Constructor<?> constructor = betDaoClazz.getDeclaredConstructor();
            betDao = (BetDaoImpl) constructor.newInstance();
        } catch (Exception e) {
            throw new AnnotationAbsentException("Can't find implementation for BetDao", e);
        }
    }

    private static Class<?> loadClass(String className) {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new AnnotationAbsentException("Can't find implementation for BetDao", e);
        }
    }

    public static UserDao getUserDao() {
        if (userDao == null) {
            if (UserDaoImpl.class.isAnnotationPresent(Dao.class)) {
                userDao = new UserDaoImpl();
            } else {
                throw new AnnotationAbsentException("UserDaoImpl" + EXCEPTION_MESSAGE);
            }
        }
        return userDao;
    }
}
