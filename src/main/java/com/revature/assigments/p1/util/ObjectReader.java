package com.revature.assigments.p1.util;

import com.revature.assigments.p1.annotations.Column;
import com.revature.assigments.p1.annotations.Entity;
import com.revature.assigments.p1.annotations.Id;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ObjectReader {


    public static ArrayList<ArrayList<Map<String,String>>> objectMap(Object object){
        Class<?> objectClass = Objects.requireNonNull(object.getClass());
        List<?> objectMap = new ArrayList<ArrayList<>>();


        if(!objectClass.isAnnotationPresent(Entity.class)){
            throw new RuntimeException(objectClass.getName() + " >> doesn't contain any @Entity annotation");
        }

        objectMap.put("Entity", objectClass.getAnnotation(Entity.class).name());

        Field[] objectClassFields = objectClass.getDeclaredFields();
        for(Field field : objectClassFields){
            field.setAccessible(true);
            Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
            for(Annotation annotation : fieldAnnotations){
                if (field.isAnnotationPresent(Id.class)){
                    objectMap.put("Id", field.getAnnotation(Id.class).name());
                }
                if(field.isAnnotationPresent(Column.class)){
                    objectMap.put("Column",field.getAnnotation(Column.class).)
                }


            }

            field.setAccessible(false);
        }


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
