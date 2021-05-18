package com.revature.p1.utils;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Session {

    private DataSource dataSource;
    private QueryBuilder queryBuilder;

    public Session() {
        dataSource = new DataSource();
        queryBuilder = new QueryBuilder();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

//    public void shutdown() {
//        try {
//            //dataSource.shutdown();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public <T> boolean save (T object) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = queryBuilder.prepareInsertQueryFromObject(object, connection);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public <T> boolean update(T object) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = queryBuilder.createUpdateQueryFromObject(object, connection);
            stmt.executeUpdate();
            return true;
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public <T, E> Object get(Class<?> clazz, T id) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException { //TODO clean this up
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(id);
        E object = (E) Class.forName(clazz.getName()).getDeclaredConstructor().newInstance();
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = queryBuilder.createSelectQueryFromClass(clazz, id, connection);

            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd =  rs.getMetaData();
            Field[] fields = clazz.getDeclaredFields();
            while (rs.next()) {

                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    Field temp = fields[i - 1];
                    temp.setAccessible(true);
                        if(temp.getAnnotation(Column.class).timestamp()) { //Feels hacky. If it is timestamp, convert to DateTime.
                            temp.set(object, rs.getTimestamp(i).toLocalDateTime());
                            temp.setAccessible(false);
                            continue;
                        }
                    temp.set(object, rs.getObject(i));
                }
            }
            return object;

        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }
}

