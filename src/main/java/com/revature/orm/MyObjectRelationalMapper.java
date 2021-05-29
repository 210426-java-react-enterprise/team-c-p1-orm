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
import java.util.stream.Collectors;

public class MyObjectRelationalMapper
{
    private final Connection connection;

    public MyObjectRelationalMapper(Connection connection)
    {
        this.connection = connection;
    }

    public <T,E> T readRow(Class<?> clazz, Pair<String, E> condition)
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
        sb.append("SELECT * ")
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

                //Build the object
                return (T) objectBuilder(clazz, resultSet,condition);

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

//    public <T,E> List<T> readRows(Class<?> clazz, Pair<String, E> condition)
//    {
//        StringBuilder sb = new StringBuilder();
//        try
//        {
//            if (clazz == null || !clazz.isAnnotationPresent(MyEntity.class))
//            {
//                throw new InvalidAnnotationException("Object is not a valid type!");
//            }
//        } catch (InvalidAnnotationException | NullPointerException e)
//        {
//            e.printStackTrace();
//        }
//        sb.append("SELECT * FROM project1.")
//          .append(clazz.getAnnotation(MyEntity.class).name())
//          .append(" WHERE ");
//        sb.append(condition.getFirst()).append("=");
//
//        .append(condition.getSecond().toString());
//
//        ResultSet resultSet = null;
//        try
//        {
//            PreparedStatement preparedStatement = connection.prepareStatement(sb.toString());
//            resultSet = preparedStatement.executeQuery();
//        } catch (SQLException e)
//        {
//            e.printStackTrace();
//        }
//        return buildSavables(savable, Objects.requireNonNull(resultSet));
//    }

//    private String findCondition(MySavable savable)
//    {
//        List<Field> fields = new ArrayList<>(Arrays.asList(savable.getClass()
//                                                                  .getDeclaredFields()));
//        for (Field field : fields)
//        {
//            if (field.isAnnotationPresent(com.revature.orm.annotations.MyColumn.class))
//            {
//                try
//                {
//                    switch (field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
//                                 .type())
//                    {
//                        case VARCHAR:
//                            field.setAccessible(true);
//                            if (!field.get(savable)
//                                      .equals(""))
//                            {
//                                String returnString = field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
//                                                           .name() + "='" + field.get(savable) + "'";
//                                field.setAccessible(false);
//                                return (returnString);
//                            }
//                            field.setAccessible(false);
//                            break;
//                        case INT:
//                        case SERIAL:
//                            field.setAccessible(true);
//                            if ((int) field.get(savable) != 0)
//                            {
//                                String returnString = field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
//                                                           .name() + "=" + field.get(savable);
//                                field.setAccessible(false);
//                                return returnString;
//                            }
//                            field.setAccessible(false);
//                            break;
//                        case DECIMAL:
//                            field.setAccessible(true);
//                            if ((double) field.get(savable) != 0)
//                            {
//                                String returnString = field.getAnnotation(com.revature.orm.annotations.MyColumn.class)
//                                                           .name() + "=" + field.get(savable);
//                                field.setAccessible(false);
//                                return returnString;
//                            }
//                            field.setAccessible(false);
//                    }
//                } catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }

    private <T,E> T objectBuilder(Class<?> clazz, ResultSet resultSet,Pair<String, E> condition)
    {
        //List of annotated class fields to populate
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        //Objects to return

        Object newObject = null;
        try
        {
            Class<?> aClass = Class.forName(clazz.getName());
            newObject = aClass.getConstructor()
                              .newInstance();
            String fieldName = null;
            for (Field field : newObject.getClass()
                                                .getDeclaredFields())
            {
                if(field.isAnnotationPresent(MyColumn.class) && field.getAnnotation(MyColumn.class).name().equals(condition.getFirst()))
                {
                    fieldName = field.getName();
                }
            }
            String setterMethod = "set" + fieldName;

            for (Method method : newObject.getClass()
                                          .getDeclaredMethods())
            {
                if (method.getName().toLowerCase().equals(setterMethod))
                {
                    method.invoke(newObject, condition.getSecond());
                }
            }

        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
        Object finalNewObject = newObject;
        try
        {
            while (resultSet.next())
            {
                //Class Methods to inspect for setters
                List<Method> methods = new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));

                //Gather the setter methods by name
                List<Method> setterMethods = methods.stream()
                                                    .filter(method -> method
                                                                              .getName()
                                                                              .toLowerCase(Locale.ROOT)
                                                                              .startsWith("set"))
                                                    .collect(Collectors.toList());

                //Match field names to setter names to invoke

                fields.forEach(field ->
                               {
                                   if (field.isAnnotationPresent(MyColumn.class))
                                   {
                                       setterMethods.forEach(method ->
                                                             {
                                                                 String methodName = method.getName()
                                                                                           .toLowerCase(Locale.ROOT)
                                                                                           .substring(3);
                                                                 String fieldName = field.getName()
                                                                                         .toLowerCase();
                                                                 if (fieldName.equals(methodName))
                                                                 {
                                                                     //Now that the field and method names match, execute the setter method for each field
                                                                     try
                                                                     {
                                                                         ColumnType fieldColumnType = field.getAnnotation(MyColumn.class)
                                                                                                           .type();
                                                                         String columnName = field.getAnnotation(MyColumn.class)
                                                                                                  .name();

                                                                         //Invoke the setter method based on each field's type
                                                                         switch (fieldColumnType)
                                                                         {
                                                                             case VARCHAR:
                                                                                 field.setAccessible(true);
                                                                                 method.invoke(finalNewObject, resultSet.getString(columnName));
                                                                                 field.setAccessible(false);
                                                                                 break;
                                                                             case INT:
                                                                             case SERIAL:
                                                                                 field.setAccessible(true);
                                                                                 method.invoke(finalNewObject, resultSet.getInt(columnName));
                                                                                 field.setAccessible(false);
                                                                                 break;
                                                                             case DECIMAL:
                                                                                 field.setAccessible(true);
                                                                                 method.invoke(finalNewObject, resultSet.getDouble(columnName));
                                                                                 field.setAccessible(false);
                                                                         }
                                                                     } catch (IllegalAccessException | InvocationTargetException | SQLException e)
                                                                     {
                                                                         e.printStackTrace();
                                                                     }
                                                                 }
                                                             });
                                   }
                               });
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return (T) finalNewObject;
    }
        public <T> void saveNewData(T object)
    {
        StringBuilder headBuilder = new StringBuilder();
        StringBuilder tailBuilder = new StringBuilder();

        try
        {
            Objects.requireNonNull(object);
            if (!object.getClass().isAnnotationPresent(MyEntity.class))
            {
                throw new InvalidAnnotationException("Object is not annotated!");
            }
            String tableName = object.getClass().getAnnotation(MyEntity.class).name();
            headBuilder.append("INSERT INTO project1.")
                       .append(tableName)
                       .append(" (");
            tailBuilder.append(" VALUES (");
            List<Field> fields = new ArrayList<>(Arrays.asList(object.getClass()
                                                                      .getDeclaredFields()));
            fields.forEach(field ->
                           {
                               if (field.isAnnotationPresent(MyColumn.class))
                               {
                                   String columnName = field.getAnnotation(MyColumn.class)
                                                            .name();
                                   if (!field.getAnnotation(MyColumn.class)
                                             .type()
                                             .equals(ColumnType.SERIAL))
                                   {
                                       headBuilder.append(columnName)
                                                  .append(",");
                                   }
                                   try
                                   {
                                       switch (field.getAnnotation(MyColumn.class)
                                                    .type())
                                       {
                                           case VARCHAR:
                                               field.setAccessible(true);
                                               tailBuilder.append("'")
                                                          .append(field.get(object))
                                                          .append("',");
                                               field.setAccessible(false);
                                               break;
                                           case SERIAL:
                                               break;
                                           case INT:
                                           case DECIMAL:
                                               field.setAccessible(true);
                                               tailBuilder.append(field.get(object))
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
            System.out.println(headBuilder);
            try
            {
                PreparedStatement preparedStatement = connection.prepareStatement(headBuilder.toString());
                preparedStatement.executeUpdate();

            } catch (SQLException e)
            {
                e.printStackTrace();
            }

        } catch (InvalidAnnotationException e)
        {
            e.printStackTrace();
        }
    }

    public <T> void updateData(T object)
    {
        StringBuilder headBuilder = new StringBuilder();
        StringBuilder conditionBuilder = new StringBuilder();

        try
        {
            if (!object.getClass()
                        .isAnnotationPresent(MyEntity.class))
            {
                throw new InvalidAnnotationException("Not a savable savable!");
            }
            String tableName = object.getClass()
                                      .getAnnotation(MyEntity.class)
                                      .name();
            headBuilder.append("UPDATE project1.")
                       .append(tableName)
                       .append(" SET ");

            List<Field> fields = new ArrayList<>(Arrays.asList(object.getClass()
                                                                      .getDeclaredFields()));
            fields.forEach(field ->
                           {
                               try
                               {
                                   if (field.isAnnotationPresent(MyColumn.class))
                                   {
                                       if (field.getAnnotation(MyColumn.class)
                                                .pk())
                                       {
                                           field.setAccessible(true);
                                           conditionBuilder.append(" WHERE ")
                                                           .append(field.getAnnotation(MyColumn.class)
                                                                        .name())
                                                           .append("=")
                                                           .append(field.getAnnotation(MyColumn.class)
                                                                        .type()
                                                                        .equals(ColumnType.VARCHAR) ? "'" : "")
                                                           .append(field.get(object))
                                                           .append(field.getAnnotation(MyColumn.class)
                                                                        .type()
                                                                        .equals(ColumnType.VARCHAR) ? "'" : "");
                                           field.setAccessible(false);
                                       }
                                       if (field.getAnnotation(MyColumn.class)
                                                .type()
                                                .equals(ColumnType.SERIAL) || field.getAnnotation(
                                               MyColumn.class)
                                                                                                                .pk() ||
                                                   field.getAnnotation(MyColumn.class)
                                                        .fk() || field.getAnnotation(MyColumn.class)
                                                                      .unique())
                                       {
                                           //System.out.println("Condition was built");
                                       }
                                       else
                                       {

                                           String columnName = field.getAnnotation(MyColumn.class)
                                                                    .name();
                                           headBuilder.append(columnName)
                                                      .append("=");

                                           switch (field.getAnnotation(MyColumn.class)
                                                        .type())
                                           {
                                               case VARCHAR:
                                                   field.setAccessible(true);
                                                   headBuilder.append("'")
                                                              .append(field.get(object))
                                                              .append("',");
                                                   field.setAccessible(false);
                                                   break;
                                               case INT:
                                               case DECIMAL:
                                                   field.setAccessible(true);
                                                   headBuilder.append(field.get(object))
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
            System.out.println(headBuilder);
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






















