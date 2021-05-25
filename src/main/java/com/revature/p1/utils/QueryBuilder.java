package com.revature.p1.utils;

import com.revature.p1.utils.annotations.Column;
import com.revature.p1.utils.annotations.Entity;
import com.revature.p1.utils.annotations.Key;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public <T> PreparedStatement createUpdateQueryFromObject(T object, Connection connection) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        Class<?> clazz = Objects.requireNonNull(object.getClass());

        if (!clazz.isAnnotationPresent(Entity.class))
            throw new IllegalArgumentException(clazz.getName() + " is not an Entity!");

        String entityName = clazz.getAnnotation(Entity.class).name();
        String tableName = entityName.isEmpty() ? clazz.getSimpleName().toLowerCase(Locale.ROOT) : entityName;
        String keyFieldName = getPrimaryField(clazz).getAnnotation(Column.class).name();
        String primaryFieldName = keyFieldName.isEmpty() ? getPrimaryField(clazz).getName().toLowerCase(Locale.ROOT) : keyFieldName;

        sb.append("update ").append(tableName).append(" set ");
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                if (field.isAnnotationPresent(Key.class))
                    continue;

                field.setAccessible(true);
                String fieldName = field.getAnnotation(Column.class).name();
                String columnName = fieldName.isEmpty() ? field.getName().toLowerCase(Locale.ROOT) : fieldName;

                sb.append(columnName).append(" = ").append(field.get(object)).append(", ");
                field.setAccessible(false);
            }
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("where ").append(primaryFieldName).append(" = ").append(getPrimaryKey(object)).append(";");

        try {
            return connection.prepareStatement(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public <T> PreparedStatement createDeleteQueryFromObject(Class<?> clazz, T id, Connection connection) {
        StringBuilder sb = new StringBuilder();
        Objects.requireNonNull(clazz);

        if (!clazz.isAnnotationPresent(Entity.class))
            throw new IllegalArgumentException(clazz.getName() + " is not an Entity");

        String entityName = clazz.getAnnotation(Entity.class).name();
        String fieldName = getPrimaryField(clazz).getAnnotation(Column.class).name();
        String primaryFieldName = fieldName.isEmpty() ? getPrimaryField(clazz).getName().toLowerCase(Locale.ROOT) : fieldName;

        sb.append("delete from ").append(entityName.isEmpty() ? clazz.getSimpleName().toLowerCase(Locale.ROOT) : entityName)
                .append(" where ").append(primaryFieldName).append(" = ").append(id).append(";");

        try {
            return connection.prepareStatement(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createTableFromObject(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        Objects.requireNonNull(clazz);

        if (!clazz.isAnnotationPresent(Entity.class))
            throw new IllegalArgumentException(clazz.getName() + " is not an Entity");

        String entityName = clazz.getAnnotation(Entity.class).name();
        String tableName = entityName.isEmpty() ? clazz.getSimpleName().toLowerCase(Locale.ROOT) : entityName;

        sb.append("create table ").append(tableName).append(" ( \n");
        
        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> sb.append(getFieldName(field)).append(" ")
                .append(getSQLType(field)).append(!field.getAnnotation(Column.class).nullable() ? " not null, \n" : ", \n"));
        sb.append("primary key ").append(getFieldName(getPrimaryField(clazz))).append(",\n);"); //will implement foreign keys later (maybe)
        sb.deleteCharAt(sb.lastIndexOf(","));

        return sb.toString();
    }

    public <T> PreparedStatement getRowsOnCondition(Class<?> clazz, T condition, String column, Connection connection) {
        Objects.requireNonNull(clazz);
        StringBuilder sb = new StringBuilder();

        if (!clazz.isAnnotationPresent(Entity.class))
            throw new IllegalArgumentException(clazz.getName() + " is not an Entity");

        String entityName = clazz.getAnnotation(Entity.class).name();
        String tableName = entityName.isEmpty() ? clazz.getSimpleName().toLowerCase(Locale.ROOT) : entityName;

        sb.append("select * from ").append(tableName).append(" where ").append(column).append(" = ").append(condition);

        try {
            return connection.prepareStatement(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    private <T> T getPrimaryKey(T object) throws IllegalAccessException {
        Class<?> clazz = Objects.requireNonNull(object.getClass());

        if (!clazz.isAnnotationPresent(Entity.class))
            throw new IllegalArgumentException(clazz.getName() + " is not an Entity");

        Field keyField = getPrimaryField(clazz);

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

    private <T> String getFieldName(Field field) {
        // so don't have to type out this ternary every time
        return field.getAnnotation(Column.class).name().isEmpty()
                ? field.getName().toLowerCase(Locale.ROOT) :
                field.getAnnotation(Column.class).name();
    }

    private String getSQLType(Field field) {
        switch (field.getType().getSimpleName()) {
            case "int":
                return SQLTypes.INT.toString();
            case "double":
                return SQLTypes.DOUBLE.toString();
            case "long":
                return SQLTypes.LONG.toString();
            case "String":
                return SQLTypes.STRING.toString();
            case "boolean":
                return SQLTypes.BOOLEAN.toString();
            case "character":
                return SQLTypes.CHARACTER.toString();
            case "BigDecimal":
                return SQLTypes.BIGDECIMAL.toString();
            case "LocalDateTime":
                return SQLTypes.LOCALDATETIME.toString();
            default:
                throw new RuntimeException("That field type is not yet mapped");
        }
    }


}
