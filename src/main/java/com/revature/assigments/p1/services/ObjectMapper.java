package com.revature.assigments.p1.services;

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

    /**
     * This method is responsible to read the all the object's annotations to mapped them into  Map
     *      MAP<Key,Value>
     *          -Key = Annotation's Element Name (for the @Table and @Id this value is replace with a their respective constant value)
     *          -Value = ArrayList of rest of annotation's elements
     *
     * @param object -- The respective object to read
     * @return Map<?,?> -- The mapped Treemap<String,ArrayList<String>>
     */
    public static Map<?,?>createObjetMap(Object object){
        Class<?> objectClass = Objects.requireNonNull(object.getClass());
        Map<String,ArrayList<String>> objectMap = new TreeMap<String,ArrayList<String>>();
        String key;


        //1.- Ensure that the respective object contains @Entity
        if(!objectClass.isAnnotationPresent(Entity.class) && !objectClass.isAnnotationPresent(Table.class)){
            throw new RuntimeException(objectClass.getName() + " >> This object must contains @Entity and @Table");
        }

        //2.-Adding the table name to the Map
        key = tableValue;

        ArrayList<String> valueTableArray = new ArrayList<>();
        valueTableArray.add(objectClass.getAnnotation(Table.class).name());
        objectMap.put(key,valueTableArray);

       //Iterating the fields to get the annotations
        Field[] objectClassFields = objectClass.getDeclaredFields();
        for(Field field : objectClassFields){
            field.setAccessible(true);
            Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
            for(Annotation annotation : fieldAnnotations){
                //3.-Adding the table id to the Map (this is optional)
                if (field.isAnnotationPresent(Id.class)){
                    key = idValue;
                    ArrayList<String> valueIdArray = new ArrayList<>();
                    valueIdArray.add(field.getAnnotation(Id.class).name());
                    objectMap.put(key, valueIdArray);
                }
                if(!field.isAnnotationPresent(Column.class)){
                    throw new RuntimeException(objectClass.getName() + " >> This object must contains @Column to be able to mapped into a DB");
                }
                ArrayList<String> supportArray = new ArrayList<>();
                key = field.getAnnotation(Column.class).name();
                supportArray.add(field.getAnnotation(Column.class).dataType());
                supportArray.add(field.getAnnotation(Column.class).unique());
                supportArray.add(field.getAnnotation(Column.class).notNull());

                objectMap.put(key ,(ArrayList)supportArray.clone());
                supportArray.clear();

               objectMap.get(key).forEach((String str) -> {
                   System.out.println(str);
               });



            }

            field.setAccessible(false);
        }

        //4.-Adding table columns to the Map

        //5.-Returning the Map

        return objectMap;
    }



    /*
    public Annotation[] readEntity(Object 0){
        Annotation[] classAnnotations = clazz.getDeclaredAnnotations();
        List<Annotation> fieldAnnotations = new ArrayList<>();
        List<Annotation> fieldAnnotations.stream().filter(Annotation -> fieldAnnotations.equals(Entity.class)).collect(Collectors.toList());
        for(Annotation annotation: classAnnotations){
            System.out.println(annotation.annotationType().getName());
            List<Field> classFields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
            for(Field field : classFields){
                fieldAnnotations.add(field.getDeclaredAnnotation(Column.class));
            }

        }
        return classAnnotations;
    }
    */
}
