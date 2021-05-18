package com.revature.assigments.p1.util;

import com.revature.assigments.p1.annotations.Column;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassReader {

    public ClassReader(){

    }

    public Annotation[] readEntity(Class<?> clazz){
        Annotation[] classAnnotations = clazz.getDeclaredAnnotations();
        List<Annotation> fieldAnnotations = new ArrayList<>();

        for(Annotation annotation: classAnnotations){
            System.out.println(annotation.annotationType().getName());
            List<Field> classFields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
            for(Field field : classFields){
                fieldAnnotations.add(field.getDeclaredAnnotation(Column.class));
            }

        }
        return classAnnotations;
    }

}
