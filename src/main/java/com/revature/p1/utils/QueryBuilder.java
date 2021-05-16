package com.revature.p1.utils;

import com.sun.corba.se.spi.ior.ObjectKey;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class QueryBuilder {

    public PreparedStatement prepareInsertQueryFromObject(Object object, Connection connection) {
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

                    stmt.setObject(counter, field.get(object));
                    counter++;
                }
            }
            return stmt;
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return stmt;
    }

}
