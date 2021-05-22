package com.revature.assigments.p1.util;

import com.revature.assigments.p1.annotations.Column;
import com.revature.assigments.p1.annotations.Entity;
import com.revature.assigments.p1.annotations.Id;
import com.revature.assigments.p1.annotations.Table;

import javax.sql.rowset.spi.SyncProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ObjectMapper {

    private static final String tableValue = "TABLE";
    private static final String idValue = "ID";
    private static final String objectValue ="OBJECT";

    /**
     * This method is responsible to read the all the object's annotations to mapped them into  Map
     *      The returned map will be use to crate the @Entity table in DB
     *      MAP<Key,Value>
     *          -Key = Annotation's Element Name (for the @Table and @Id this value is replace with a their respective constant value)
     *          -Value = ArrayList of rest of annotation's elements
     *
     * @param object -- The respective object to read
     * @return Map<?,?> -- The mapped Treemap<String,ArrayList<String>>
     */
    public static Map<?,?>createObjetMapForDB(Object object) {

        Class<?> objectClass = Objects.requireNonNull(object.getClass());

        //1.- Ensure that the respective object contains @Entity
        if (!objectClass.isAnnotationPresent(Entity.class) && !objectClass.isAnnotationPresent(Table.class)) {
            throw new RuntimeException(objectClass.getName() + " >> This object must contain @Entity and @Table to be mapped");
        }

        Map<String, ArrayList<String>> objectMap = new TreeMap<String, ArrayList<String>>();
        String key;
        ArrayList<String> supportArray = new ArrayList<>();

        //2.-Adding the table name to the Map
        key = tableValue;

        supportArray.add(objectClass.getAnnotation(Table.class).name());
        objectMap.put(key, (ArrayList) supportArray.clone());
        supportArray.clear();

        //Iterating the fields to get the annotations
        Field[] objectClassFields = objectClass.getDeclaredFields();
        for (Field field : objectClassFields) {
            field.setAccessible(true);
            Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
            for (Annotation annotation : fieldAnnotations) {
                //3.-Adding the table id to the Map (this is optional)

                if (field.isAnnotationPresent(Id.class)) {
                    key = idValue;

                    supportArray.add(field.getAnnotation(Id.class).name());
                    objectMap.put(key, (ArrayList) supportArray.clone());
                    supportArray.clear();
                }
                //4.-Adding table columns to the Map
                if (!field.isAnnotationPresent(Column.class)) {
                    throw new RuntimeException(objectClass.getName() + " >> This object must contains @Column to be able to mapped into a DB");
                }

                key = field.getAnnotation(Column.class).name();
                supportArray.add(field.getAnnotation(Column.class).dataType());
                supportArray.add(field.getAnnotation(Column.class).unique());
                supportArray.add(field.getAnnotation(Column.class).notNull());

                objectMap.put(key, (ArrayList) supportArray.clone());
                supportArray.clear();

                // How to access the Array inside my map
               /*objectMap.get(key).forEach((String str) -> {
                   System.out.println(str);
               });*/

            }

            field.setAccessible(false);
        }

        //5.-Returning the Map

        return objectMap;

    }
    /**
     * This method is responsible to read the all the object's annotations to mapped them into  Map
     *      The returned map will be use to insert the current values in memory to the @Entity table in DB
     *      MAP<Key,Value>
     *          -Key = Annotation's Element Name (for the @Table and @Id this value is replace with a their respective constant value)
     *          -Value = ArrayList of rest of annotation's elements
     *
     * @param object -- The respective object to read
     * @return Map<?,?> -- The mapped Treemap<String,ArrayList<String>>
     */
    public static Map<?,?>createInstanceMapForDB(Object object){

        Class<?> objectClass = Objects.requireNonNull(object.getClass());

        //1.- Ensure that the respective object contains @Entity
        if(!objectClass.isAnnotationPresent(Entity.class) && !objectClass.isAnnotationPresent(Table.class)){
            throw new RuntimeException(objectClass.getName() + " >> This object must contain @Entity and @Table to be mapped");
        }

        Map<String,ArrayList<String>> instanceMap = new HashMap<String,ArrayList<String>>();
        String key;
        ArrayList<String> supportArray = new ArrayList<>();

        //2.-Adding the instance name to the Map
        key = objectValue;

        supportArray.add(objectClass.getAnnotation(Table.class).name());
        instanceMap.put(key ,(ArrayList)supportArray.clone());


        //Iterating the fields to get their values
        Field[] objectClassFields = objectClass.getDeclaredFields();
        for(Field field : objectClassFields) {
            field.setAccessible(true);
            Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
            for (Annotation annotation : fieldAnnotations) {
                //3.-Adding table columns to the Map

                if (!field.isAnnotationPresent(Column.class)) {
                    throw new RuntimeException(objectClass.getName() + " >> This instance must contains @Column to be able to mapped into a DB");
                }

                key = field.getAnnotation(Column.class).name();
                supportArray.add(field.getType().getTypeName());
                try{
                    supportArray.add(field.get(object).toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }


                instanceMap.put(key, (ArrayList) supportArray.clone());
                supportArray.clear();

                // How to access the Array inside my map
               /*objectMap.get(key).forEach((String str) -> {
                   System.out.println(str);
               });*/

            }

            field.setAccessible(false);
        }

        //5.-Returning the Map
        return instanceMap;
    }

    /**
     * This method is responsible to read all the fields from an object to build query's fields sequence
     *
     * @param object -- The respective object to read
     * @return ArrayList<String> with the sequence
     */
    public static ArrayList<String> objectFieldSequence(Object object){

        Class<?> objectClass = Objects.requireNonNull(object.getClass());

        //1.- Ensure that the respective object contains @Entity
        if (!objectClass.isAnnotationPresent(Entity.class) && !objectClass.isAnnotationPresent(Table.class)) {
            throw new RuntimeException(objectClass.getName() + " >> This object must contain @Entity and @Table to be mapped");
        }
        ArrayList<String> sequence = new ArrayList<String>();

        //Iterating the fields to get the annotations
        Field[] objectClassFields = objectClass.getDeclaredFields();
        for (Field field : objectClassFields) {
            field.setAccessible(true);
            Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
            for (Annotation annotation : fieldAnnotations) {
                //2.-Adding @Column to the sequence

                if (!field.isAnnotationPresent(Column.class)) {
                    throw new RuntimeException(objectClass.getName() + " >> This object must contains @Column to be able to mapped into a DB");
                }

                String[] support = String.valueOf(annotation).split("\\.");
                int supportPos = (support.length)-1;
                String str = String.valueOf(support[supportPos].subSequence(0,2));
                String strComp = "Id";
                if(!str.equals(strComp)){
                    sequence.add(field.getAnnotation(Column.class).name());
                }
            }

            field.setAccessible(false);
        }

        //3.-Returning the ArrayList

        return sequence;
    }


}
