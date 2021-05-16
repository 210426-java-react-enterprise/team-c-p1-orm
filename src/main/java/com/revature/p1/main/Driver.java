package com.revature.p1.main;

import com.revature.p1.models.Account;
import com.revature.p1.models.AppUser;
import com.revature.p1.models.User;
import com.revature.p1.utils.Column;
import com.revature.p1.utils.Entity;
import com.revature.p1.utils.Pk;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

public class Driver {

    public static void main(String[] args) {
        // ClassUtils utils = new ClassUtils();
        User user = new User(1, "some", "guy");
        AppUser user1 = new AppUser(1, "someguy", "1234", "sguy@gmail.com", "Some", "Guy", LocalDateTime.now(), 34);
        Account account = new Account(1, "checking", 100);
        // Account account = new Account();

        Driver driver = new Driver();

        //driver.doStuffWithObject(account);
        try {
            System.out.println(driver.createInsertQueryFromObject(user));
            System.out.println(driver.createInsertQueryFromObject(user1));
            System.out.println(driver.createInsertQueryFromObject(account));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public <T> void doStuffWithObject(Object object) {
        Class<?> oClass = Objects.requireNonNull(object.getClass());
        Field[] fields = oClass.getDeclaredFields();

        System.out.println("Extracting data from object...");
        System.out.println("Class name: " + oClass.getSimpleName());
        for (Field field : fields) {
//            Column annotation = field.getAnnotation(Column.class);
            System.out.println(field.getAnnotation(Column.class).name());
            System.out.println(field.isAnnotationPresent(Column.class));
            System.out.println(field.isAnnotationPresent(Pk.class));
        }

        System.out.println("=================================");
    }

    public String createInsertQueryFromObject(Object object) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        Class<?> oClass = Objects.requireNonNull(object.getClass());
        Class<Pk> id = Pk.class;
        Class<Column> column = Column.class;

        if (!oClass.isAnnotationPresent(Entity.class))
            throw new IllegalArgumentException("This object is not an Entity!");

        String entityName = oClass.getAnnotation(Entity.class).name();

        sb.append("insert into ").append(entityName.isEmpty() ? oClass.getSimpleName().toLowerCase(Locale.ROOT) : entityName).append(" values (");
        Field[] fields = oClass.getDeclaredFields();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(id) && !field.isAnnotationPresent(column))
                throw new IllegalArgumentException(field.getName() + " does not have an annotation.");
            field.setAccessible(true);
            sb.append(field.get(object)).append(", ");
            field.setAccessible(false);
        }

        String finalString = sb.toString();

        return finalString.substring(0, finalString.length() - 2).concat(");");
    }
}
