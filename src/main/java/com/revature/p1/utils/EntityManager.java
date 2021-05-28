package com.revature.p1.utils;

import com.revature.p1.exceptions.ObjectNotFoundException;
import com.revature.p1.exceptions.ResourcePersistenceException;
import com.revature.p1.repos.DataSource;
import com.revature.p1.utils.annotations.Column;
import jdk.nashorn.internal.objects.annotations.Where;

import javax.xml.crypto.Data;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class EntityManager {

    private final QueryBuilder queryBuilder = new QueryBuilder();

    public EntityManager() {
    }

    public Connection getConnection() {
        try {
            return DataSource.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T, E> Object get(Class<?> clazz, T id) { //TODO clean this up
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(id);
        try {
            E object = (E) Class.forName(clazz.getName()).getDeclaredConstructor().newInstance();
            Connection connection = DataSource.getInstance().getConnection();
            PreparedStatement stmt = queryBuilder.createSelectQueryFromClass(clazz, id, connection);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            Field[] fields = clazz.getDeclaredFields();
            while (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    Field temp = fields[i - 1];
                    temp.setAccessible(true);
                    if (temp.getAnnotation(Column.class).isDouble()) {
                        temp.set(object, rs.getDouble(i));
                        temp.setAccessible(false);
                        continue;
                    }
                    temp.set(object, rs.getObject(i));
                    temp.setAccessible(false);
                }
            }
            DataSource.getInstance().releaseConnection(connection);
            return object;

        } catch (SQLException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (InvocationTargetException | InstantiationException e) {
            throw new ObjectNotFoundException("Something went wrong while retrieving the object. (" + e.getMessage() + ")");
        }
        return null;
    }


    public <T> boolean update(T object) {
        try {
            Connection connection = DataSource.getInstance().getConnection();
            PreparedStatement stmt = queryBuilder.createUpdateQueryFromObject(object, connection);
            stmt.executeUpdate();
            DataSource.getInstance().releaseConnection(connection);
            return true;
        } catch (SQLException | IllegalAccessException e) {
            throw new ResourcePersistenceException("Something went wrong while updating the object. (" + e.getMessage() + ")");
        }
    }

    public <T> T save(T object) {
        try {
            Connection connection = DataSource.getInstance().getConnection();
            PreparedStatement stmt = queryBuilder.prepareInsertQueryFromObject(object, connection);
            int rows = stmt.executeUpdate();

            if(rows != 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                while (rs.next()) {
                    Field field = queryBuilder.getPrimaryField(object.getClass());
                    Method method = Arrays.stream(object.getClass().getDeclaredMethods()).
                            filter(method1 -> method1.getName().substring(3).toLowerCase(Locale.ROOT).equals(field.getName().toLowerCase(Locale.ROOT)))
                            .filter(method1 -> method1.getName().startsWith("set"))
                            .findFirst()
                            .orElseThrow(() -> new ObjectNotFoundException("unable to locate method field"));
                    field.setAccessible(true);
                    method.invoke(object, rs.getObject(queryBuilder.getFieldName(queryBuilder.getPrimaryField(object.getClass())))); //gross
                    field.setAccessible(false);
                }
            }
            return object;
        } catch (SQLException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new ResourcePersistenceException("Something went wrong while saving the object. (" + e.getMessage() + ")");
        }
    }

    public <T> boolean delete(Class<?> clazz, T id) {
        try {
            Connection connection = DataSource.getInstance().getConnection();
            PreparedStatement stmt = queryBuilder.createDeleteQueryFromObject(clazz, id, connection);
            stmt.executeUpdate();
            DataSource.getInstance().releaseConnection(connection);
            return true;
        } catch (SQLException e) {
            throw new ResourcePersistenceException("Something went wrong while saving the object. (" + e.getMessage() + ")");
        }
    }

    @SuppressWarnings("unchecked")
    public <T, E> List<E> getAllOnCondition(Class<?> clazz, String column, T condition) {

        try {

            List<Object> objects = new ArrayList<>();
            Connection connection = DataSource.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(queryBuilder.getRowsOnCondition(clazz, condition, column));
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            Field[] fields = clazz.getDeclaredFields();
            E object;

            while (rs.next()) {
                object = (E) Class.forName(clazz.getName()).getDeclaredConstructor().newInstance();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    Field temp = fields[i - 1];
                    temp.setAccessible(true);
                    if (temp.getAnnotation(Column.class).isDouble()) {
                        temp.set(object, rs.getDouble(i));
                        temp.setAccessible(false);
                        continue;
                    }
                    temp.set(object, rs.getObject(i));
                    temp.setAccessible(false);
                }
                objects.add(object);
            }
            DataSource.getInstance().releaseConnection(connection);
            return (List<E>) objects;

        } catch (SQLException | ClassNotFoundException |
                NoSuchMethodException | InvocationTargetException |
                InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new ObjectNotFoundException("Something went wrong while retrieving the object(s). (" + e.getMessage() + ")");
        }
    }

    @SuppressWarnings("unchecked")
    public <T, E> E getOneOnCondition(Class<?> clazz, String column, T condition) {

        try {
            Connection connection = DataSource.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(queryBuilder.getRowsOnCondition(clazz, condition, column));
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            Field[] fields = clazz.getDeclaredFields();
            E object = null;

            if (rs.next()) {
                object = (E) Class.forName(clazz.getName()).getDeclaredConstructor().newInstance();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    Field temp = fields[i - 1];
                    temp.setAccessible(true);
                    if (temp.getAnnotation(Column.class).isDouble()) {
                        temp.set(object, rs.getDouble(i));
                        temp.setAccessible(false);
                        continue;
                    }
                    temp.set(object, rs.getObject(i));
                    temp.setAccessible(false);
                }
            }
            DataSource.getInstance().releaseConnection(connection);
            return object;
        } catch (SQLException | ClassNotFoundException |
                NoSuchMethodException | InvocationTargetException |
                InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new ObjectNotFoundException("Something went wrong while retrieving the object. (" + e.getMessage() + ")");
        }
    }
}
