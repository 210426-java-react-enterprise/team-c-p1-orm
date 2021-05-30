package com.revature.assigments.orm.util;

import com.revature.assigments.orm.annotations.Column;
import com.revature.assigments.orm.annotations.Entity;
import com.revature.assigments.orm.annotations.Id;
import com.revature.assigments.orm.annotations.Table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.lang.Character;
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
    public static Map<?,?>createClassMapForDB(Object object) {

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
    public static<T> Map<?,?>createObjectMapForDB(T object){

        Class<?> objectClass = Objects.requireNonNull(object.getClass());
        Map<String,ArrayList<String>> instanceMap = new HashMap<String,ArrayList<String>>();

        String key;
        ArrayList<String> supportArray = new ArrayList<>();

        //1.-Adding the instance name to the Map
        key = objectValue;

        supportArray.add(objectClass.getAnnotation(Table.class).name());
        instanceMap.put(key ,(ArrayList)supportArray.clone());

        //2.-Iterating the fields to get their values
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
                    }catch (NullPointerException e){
                        supportArray.add(null);//If the field is empty return null
                    }catch (Exception e2) {
                        e2.printStackTrace();
                    }

                instanceMap.put(key, (ArrayList) supportArray.clone());
                supportArray.clear();

            }

            field.setAccessible(false);

        }

        //4.-Returning the Map
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
        ArrayList<String> sequence = new ArrayList<String>();
        //1.-Iterating the fields to get the annotations
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

    /**
     * This method verify is the object class was annotated as @Entity and @Table if not return an exception
     * @param object
     * @throws RuntimeException
     */
    public static void verifyObjectClass(Object object) throws RuntimeException{
        Class<?> objectClass = Objects.requireNonNull(object.getClass());

        //1.- Ensure that the respective object contains @Entity
        if(!objectClass.isAnnotationPresent(Entity.class) && !objectClass.isAnnotationPresent(Table.class)){
            throw new RuntimeException(objectClass.getName() + " >> This object must contain @Entity and @Table to be mapped");
        }
    }
    
    /**
     *
     * @param object
     * @param objectFieldsValuesRequestedFromDB
     * @param <T>
     * @return
     */
    
    public static <T> Object updateNewInstance(Object object, HashMap<String, ArrayList<String>> objectFieldsValuesRequestedFromDB){
        Class<?> objectClass = Objects.requireNonNull(object.getClass());

        //1.- Ensure that the respective object contains @Entity
        try{
            verifyObjectClass(object);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return null;
        }
    
        //2.-Iterate every field from the class and update the values with the Map
        Field[] objectClassFields = objectClass.getDeclaredFields();
        for (Field field : objectClassFields) {
            field.setAccessible(true);
            Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
            for (Annotation annotation : fieldAnnotations) {
                //2.-Adding @Column to the sequence

                if (!field.isAnnotationPresent(Column.class)) {
                    throw new RuntimeException(objectClass.getName() + " >> This object must contains @Column to be able to mapped into a DB");
                }
                StringBuilder methodRequested = new StringBuilder("set"+field.getName());
                Method[] objectMethods = objectClass.getMethods();
                
//                String testStr = objectMethods[1].getName().toLowerCase(Locale.ROOT);
//                String testStr2 = Arrays.stream(objectMethods).findFirst().get().getName();
                
                for (int i = 0; i < Arrays.stream(objectMethods).count(); i++) {
                    if (objectMethods[i].getName().toLowerCase(Locale.ROOT).equals(methodRequested.toString())){
                        try {
                            
                            String[] stringArray = String.valueOf(field.getName()).split("\\.");
                            int methodNamePos = (stringArray.length)-1;
                            
                            char[] charArray = new char[stringArray[methodNamePos].length()];
                            charArray = stringArray[methodNamePos].toString().toCharArray();
                            charArray[0]=Character.toUpperCase(charArray[0]);
                            String simpleFieldName = String.valueOf(charArray);
                            
                            StringBuilder simpleMethodName = new StringBuilder();
                            simpleMethodName.append("set").append(simpleFieldName);
                            
                            String finalMethodName = String.valueOf(simpleMethodName);
                            
                            StringBuilder fieldClass = new StringBuilder();
                            fieldClass.append(field.getType()).append(".class");
                            
                            objectMethods[i].invoke(object,_);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } };
                    }
                }
                
//                Arrays.stream(objectClass.getMethods()).filter(method -> {
//                                                                            return method.getName()..toLowerCase(Locale.ROOT).equals(methodRequested.toString().replace("_", ""));
//                                                                         }).forEach(method -> {
//                                                                                                try {
//                                                                                                    method.invoke(object, objectFieldsValuesRequestedFromDB.get(field.getName()).get(1));
//                                                                                                } catch (IllegalAccessException e) {
//                                                                                                    e.printStackTrace();
//                                                                                                } catch (InvocationTargetException e) {
//                                                                                                    e.printStackTrace();
//                                                                                                } });
    
            field.setAccessible(false);
            }
        
        return object;
    }

}
