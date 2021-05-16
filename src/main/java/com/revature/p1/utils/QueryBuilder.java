package com.revature.p1.utils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class QueryBuilder {

    public <T> PreparedStatement prepareInsertQueryFromObject(T object, Connection connection) { //Connection to come Soon™
        StringBuilder sb = new StringBuilder();
        Class<?> oClass = Objects.requireNonNull(object.getClass());
        PreparedStatement stmt = null;

        if (!oClass.isAnnotationPresent(Entity.class))
            throw new IllegalArgumentException("This class is not an entity!");

        Field[] fields = oClass.getDeclaredFields();
        String entityName = oClass.getAnnotation(Entity.class).name();

        sb.append("insert into ").append(entityName.isEmpty() ? oClass.getSimpleName().toLowerCase(Locale.ROOT) : entityName)
                .append("(");

        for (Field field : fields) { //Append field names to statement
            if (field.isAnnotationPresent(Column.class)) {
                if (field.isAnnotationPresent(Key.class))
                    continue;

                String name = field.getAnnotation(Column.class).name();
                sb.append(name.isEmpty() ? field.getName().toLowerCase(Locale.ROOT) : name).append(", ");
            }
        }

        sb.deleteCharAt(sb.lastIndexOf(",")).deleteCharAt(sb.lastIndexOf(" "))
                .append(") ").append("\n").append("values(");

        for (Field field : fields) { //Append a ? in the values section for every field
            if (field.isAnnotationPresent(Column.class)) {
                if (field.isAnnotationPresent(Key.class))
                    continue;

                sb.append("?, ");
            }
        }
        sb.deleteCharAt(sb.lastIndexOf(",")).deleteCharAt(sb.lastIndexOf(" ")).append(");");

        try { //set objects for every field
            stmt = connection.prepareStatement(sb.toString());
            int counter = 1;
            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    if (field.isAnnotationPresent(Key.class))
                        continue;
                    field.setAccessible(true);
                    stmt.setObject(counter, field.get(object));
                    field.setAccessible(false);
                    counter++;
                }
            }
            return stmt;
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return stmt;
    }

    public <T> PreparedStatement createSelectQueryFromClass(Class<?> clazz, T id, Connection connection) {
        StringBuilder sb = new StringBuilder();
        Objects.requireNonNull(clazz);


        if (!clazz.isAnnotationPresent(Entity.class))
            throw new IllegalArgumentException(clazz.getName() + " is not an Entity");

        String entityName = clazz.getAnnotation(Entity.class).name();
        String fieldName = getPrimaryField(clazz).getAnnotation(Column.class).name();
        String primaryFieldName = fieldName.isEmpty() ? getPrimaryField(clazz).getName().toLowerCase(Locale.ROOT) : fieldName;

        sb.append("select * from ").append(entityName.isEmpty() ? clazz.getSimpleName().toLowerCase(Locale.ROOT) : entityName)
        .append(" where ").append(primaryFieldName).append(" = ").append(id).append(";");

        try {
            return connection.prepareStatement(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T getPrimaryKey(T object) throws IllegalAccessException { //not useless yet, but I was createSelectQueryFromClass wrong
        Class<?> oClass = Objects.requireNonNull(object.getClass());

        if (!oClass.isAnnotationPresent(Entity.class))
            throw new IllegalArgumentException(oClass.getName() + " is not an Entity");

        Field keyField = getPrimaryField(oClass);

        keyField.setAccessible(true);
        T key = (T) keyField.get(object);
        keyField.setAccessible(false);

        return key;
    }

    private Field getPrimaryField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Key.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("This entity does not have a primary key"));
    }

}
