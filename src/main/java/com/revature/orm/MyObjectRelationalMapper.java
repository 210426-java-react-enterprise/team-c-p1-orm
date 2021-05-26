package com.revature.orm;

import com.revature.orm.annotations.ColumnType;
import com.revature.orm.annotations.MyColumn;
import com.revature.orm.annotations.MyEntity;
import com.revature.orm.exceptions.InvalidAnnotationException;
import com.revature.orm.exceptions.ObjectConditionMismatchException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MyObjectRelationalMapper
{
    private final Connection connection;

    public MyObjectRelationalMapper(Connection connection)
    {
        this.connection = connection;
    }

    public <T, E> T readRow(Class<?> clazz, Pair<String, E> condition)
    {
        //Check for object validity, nullness and annotation
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(condition);
        try
        {
            if (!clazz.isAnnotationPresent(MyEntity.class))
            {
                throw new InvalidAnnotationException("Object is not a properly annotated type!");
            }
        } catch (InvalidAnnotationException e)
        {
            e.printStackTrace();
            return null;
        }

        List<Field> classFields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        //Validating object / condition
        List<Field> fields = classFields.stream()
                                        //Filtering annotated fields
                                        .filter(field -> field.isAnnotationPresent(MyColumn.class))
                                        //Filtering matching field
                                        .filter(field -> field.getAnnotation(MyColumn.class)
                                                              .name()
                                                              .equals(condition.getFirst()))
                                        .collect(Collectors.toList());
        //Check for more than result found
        if (fields.size() == 0)
        {
            try
            {
                throw new ObjectConditionMismatchException();
            } catch (ObjectConditionMismatchException e)
            {
                e.printStackTrace();
            }
        }
        //Build SQL query
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ")
          .append(clazz.getAnnotation(MyEntity.class)
                       .name())
          .append(".")
          .append(condition.getFirst())
          .append(" FROM project1.")
          .append(clazz.getAnnotation(MyEntity.class)
                       .name())
          .append(" WHERE ")
          .append(condition.getFirst())
          .append("=")
          .append(fields.get(0)
                        .getAnnotation(MyColumn.class)
                        .type()
                        .equals(ColumnType.VARCHAR) ? "'" : "")
          .append(condition.getSecond()
                           .toString())
          .append(fields.get(0)
                        .getAnnotation(MyColumn.class)
                        .type()
                        .equals(ColumnType.VARCHAR) ? "'" : "");

        System.out.println(sb);
        //Query the DB
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sb.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                //Build the object
                return (T) objectBuilder(clazz, resultSet).get(0);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;

    }

    public List<MySavable> readRows(MySavable savable)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            if (!savable.getClass()
                        .isAnnotationPresent(com.revature.orm.annotations.MyEntity.class))
            {
                throw new InvalidAnnotationException("Object is not a 'Savable' type!");
            }
        } catch (InvalidAnnotationException e)
        {
            e.printStackTrace();
        }
        sb.append("SELECT * FROM project1.")
          .append(savable.getClass()
                         .getAnnotation(com.revature.orm.annotations.MyEntity.class)
                         .name())
          .append(" WHERE ");
        sb.append(Objects.requireNonNull(findCondition(savable)));

        ResultSet resultSet = null;
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sb.toString());
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return buildSavables(savable, Objects.requireNonNull(resultSet));
    }

    private String findCondition(MySavable savable)
    {
        List<Field> fields = new ArrayList<>(Arrays.asList(savable.getClass()
                                                                  .getDeclaredFields()));
        for (Field field : fields)
        {
            if (field.isAnnotationPresent(com.revature.orm.annotations.MyColumn.class))
            {
                try
                {
                    switch (field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                 .type())
                    {
                        case VARCHAR:
                            field.setAccessible(true);
                            if (!field.get(savable)
                                      .equals(""))
                            {
                                String returnString = field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                           .name() + "='" + field.get(savable) + "'";
                                field.setAccessible(false);
                                return (returnString);
                            }
                            field.setAccessible(false);
                            break;
                        case INT:
                        case SERIAL:
                            field.setAccessible(true);
                            if ((int) field.get(savable) != 0)
                            {
                                String returnString = field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                           .name() + "=" + field.get(savable);
                                field.setAccessible(false);
                                return returnString;
                            }
                            field.setAccessible(false);
                            break;
                        case DECIMAL:
                            field.setAccessible(true);
                            if ((double) field.get(savable) != 0)
                            {
                                String returnString = field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                           .name() + "=" + field.get(savable);
                                field.setAccessible(false);
                                return returnString;
                            }
                            field.setAccessible(false);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private <T> List<T> objectBuilder(Class<?> clazz, ResultSet resultSet)
    {
        //List of annotated class fields to populate
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        //Objects to return
        List<T> objects = new ArrayList<>();
        try
        {
            while (resultSet.next())
            {
                //Class Methods to inspect for setters
                List<Method> methods = new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));

                //Search for setter methods by name
                List<Method> setterMethods = methods.stream()
                                                    .filter(method ->
                                                                    method.getName()
                                                                          .toLowerCase(Locale.ROOT)
                                                                          .startsWith("set"))
                                                    .collect(Collectors.toList());
                fields.forEach(field -> {
                    setterMethods.forEach(method ->
                                {
                                    if (method.getName()
                                              .toLowerCase(Locale.ROOT)
                                              .startsWith("set"))
                                    {
                                        String methodName = method.getName()
                                                                  .toLowerCase(Locale.ROOT)
                                                                  .substring(3);
                                        if (field.getName()
                                                 .toLowerCase(Locale.ROOT)
                                                 .equals(methodName))
                                        {
                                            //System.out.println("field: " + field.getName() + "\tmethod: " + method.getName());
                                            try
                                            {
                                                switch (field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                             .type())
                                                {
                                                    case VARCHAR:
                                                        field.setAccessible(true);
                                                        method.invoke(savable,
                                                                      resultSet.getString(field.getAnnotation(
                                                                              com.revature.orm.annotations.MyColumn.class)
                                                                                               .name()));
                                                        field.setAccessible(false);
                                                        break;
                                                    case INT:
                                                    case SERIAL:
                                                        field.setAccessible(true);
                                                        method.invoke(savable,
                                                                      resultSet.getInt(field.getAnnotation(
                                                                              com.revature.orm.annotations.MyColumn.class)
                                                                                            .name()));
                                                        field.setAccessible(false);
                                                        break;
                                                    case DECIMAL:
                                                        field.setAccessible(true);
                                                        method.invoke(savable,
                                                                      resultSet.getDouble(field.getAnnotation(
                                                                              com.revature.orm.annotations.MyColumn.class)
                                                                                               .name()));
                                                        field.setAccessible(false);
                                                }

                                                //method.invoke(savable, resultSet.getString(field.getAnnotation(MyColumn.class).name()));
                                                field.setAccessible(false);

                                            } catch (IllegalAccessException | InvocationTargetException | SQLException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });


                Class<?> clazz = Class.forName(savable.getClass()
                                                      .getName());
                Object newObject = clazz.getConstructor(MySavable.class)
                                        .newInstance(savable);
                savables.add((MySavable) newObject);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return savables;
    }

    public int saveNewData(MySavable savable)
    {
        AtomicInteger rowsAffected = new AtomicInteger();
        StringBuilder headBuilder = new StringBuilder();
        StringBuilder tailBuilder = new StringBuilder();
        try
        {
            if (!savable.getClass()
                        .isAnnotationPresent(com.revature.orm.annotations.MyEntity.class))
            {
                throw new InvalidAnnotationException("Not a savable savable!");
            }
            String tableName = savable.getClass()
                                      .getAnnotation(com.revature.orm.annotations.MyEntity.class)
                                      .name();
            headBuilder.append("INSERT INTO project1.")
                       .append(tableName)
                       .append(" (");
            tailBuilder.append(" VALUES (");
            List<Field> fields = new ArrayList<>(Arrays.asList(savable.getClass()
                                                                      .getDeclaredFields()));
            fields.forEach(field ->
                           {
                               if (field.isAnnotationPresent(com.revature.orm.annotations.MyColumn.class))
                               {


                                   String columnName = field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                            .name();
                                   if (!field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                             .type()
                                             .equals(com.revature.orm.annotations.ColumnType.SERIAL))
                                   {
                                       headBuilder.append(columnName)
                                                  .append(",");
                                   }
                                   try
                                   {
                                       switch (field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                    .type())
                                       {
                                           case VARCHAR:
                                               field.setAccessible(true);
                                               tailBuilder.append("'")
                                                          .append(field.get(savable))
                                                          .append("',");
                                               field.setAccessible(false);
                                               break;
                                           case SERIAL:
                                               break;
                                           case INT:
                                           case DECIMAL:
                                               field.setAccessible(true);
                                               tailBuilder.append(field.get(savable))
                                                          .append(",");
                                               field.setAccessible(false);
                                               break;
                                       }
                                   } catch (Exception e)
                                   {
                                       e.printStackTrace();
                                   }
                               }
                           });
            headBuilder.deleteCharAt(headBuilder.length() - 1);
            tailBuilder.deleteCharAt(tailBuilder.length() - 1);
            headBuilder.append(")");
            tailBuilder.append(")");

            headBuilder.append(tailBuilder);
            try
            {
                PreparedStatement preparedStatement = connection.prepareStatement(headBuilder.toString());
                rowsAffected.addAndGet(preparedStatement.executeUpdate());

            } catch (SQLException e)
            {
                e.printStackTrace();
                return -1;
            }
            headBuilder.setLength(0);
            tailBuilder.setLength(0);


        } catch (InvalidAnnotationException e)
        {
            e.printStackTrace();
        }
        return rowsAffected.get();
    }

    public void updateData(MySavable savable)
    {
        StringBuilder headBuilder = new StringBuilder();
        StringBuilder conditionBuilder = new StringBuilder();

        try
        {
            if (!savable.getClass()
                        .isAnnotationPresent(com.revature.orm.annotations.MyEntity.class))
            {
                throw new InvalidAnnotationException("Not a savable savable!");
            }
            String tableName = savable.getClass()
                                      .getAnnotation(com.revature.orm.annotations.MyEntity.class)
                                      .name();
            headBuilder.append("UPDATE project1.")
                       .append(tableName)
                       .append(" SET ");

            List<Field> fields = new ArrayList<>(Arrays.asList(savable.getClass()
                                                                      .getDeclaredFields()));
            fields.forEach(field ->
                           {
                               try
                               {
                                   if (field.isAnnotationPresent(com.revature.orm.annotations.MyColumn.class))
                                   {
                                       if (field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                .pk())
                                       {
                                           field.setAccessible(true);
                                           conditionBuilder.append(" WHERE ")
                                                           .append(field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                                        .name())
                                                           .append("=")
                                                           .append(field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                                        .type()
                                                                        .equals(com.revature.orm.annotations.ColumnType.VARCHAR) ? "'" : "")
                                                           .append(field.get(savable))
                                                           .append(field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                                        .type()
                                                                        .equals(com.revature.orm.annotations.ColumnType.VARCHAR) ? "'" : "");
                                           field.setAccessible(false);
                                       }
                                       if (field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                .type()
                                                .equals(com.revature.orm.annotations.ColumnType.SERIAL) || field.getAnnotation(
                                               com.revature.orm.annotations.MyColumn.class)
                                                                                                                .pk() ||
                                                   field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                        .fk() || field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                                      .unique())
                                       {
                                           //System.out.println("Condition was built");
                                       }
                                       else
                                       {

                                           String columnName = field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                                    .name();
                                           headBuilder.append(columnName)
                                                      .append("=");

                                           switch (field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
                                                        .type())
                                           {
                                               case VARCHAR:
                                                   field.setAccessible(true);
                                                   headBuilder.append("'")
                                                              .append(field.get(savable))
                                                              .append("',");
                                                   field.setAccessible(false);
                                                   break;
                                               case INT:
                                               case DECIMAL:
                                                   field.setAccessible(true);
                                                   headBuilder.append(field.get(savable))
                                                              .append(",");
                                                   field.setAccessible(false);
                                                   break;
                                           }
                                       }
                                   }
                               } catch (Exception e)
                               {
                                   e.printStackTrace();
                               }
                           });

            headBuilder.deleteCharAt(headBuilder.length() - 1);
            headBuilder.append(conditionBuilder);

            try
            {
                PreparedStatement preparedStatement = connection.prepareStatement(headBuilder.toString());
                preparedStatement.executeUpdate();

            } catch (SQLException e)
            {
                e.printStackTrace();
                return;
            }
            headBuilder.setLength(0);

        } catch (InvalidAnnotationException e)
        {
            e.printStackTrace();
        }
    }


}






















