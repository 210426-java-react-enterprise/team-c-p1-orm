package com.revature.assigments.orm.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


/**
 * This is a utility class to bring classes from packages
 */
public class PackagesReader {

    public static List<Class<?>> getClassesInPackageWithConstraints(String packageName, Predicate<Class<?>> predicate) throws MalformedURLException, ClassNotFoundException {
        List<Class<?>> packageClasses = new ArrayList<>();
        List<String> classNames = new ArrayList<>();

        File packageDirectory = new File("target/classes/" + packageName.replace('.','/'));

        for(File file : Objects.requireNonNull(packageDirectory.listFiles())){
            if(file.isDirectory()){
                packageClasses.addAll(getClassesInPackageWithConstraints(packageName + "target/classes/",predicate));
            }else if(file.getName().contains(".class")){
                classNames.add(file.getName());
            }

        }

        URLClassLoader ucl = new URLClassLoader(new URL[]{new File("target/classes.").toURI().toURL()});

        for(String className : classNames){
            Class<?> clazz = ucl.loadClass(packageName + "." + className.substring(0,className.length()-6));
            if(predicate.test(clazz)){
                packageClasses.add(clazz);
            }
        }

        return packageClasses;
    }

    public static List<Class<?>> getClassesInPackage(String packageName) throws MalformedURLException, ClassNotFoundException {
        List<Class<?>> packageClasses = new ArrayList<>();
        List <String> classNames = new ArrayList<>();

        String s = "target/classes/" + packageName.replace('.', '/');
        System.out.println(s);

        File packageDirectory = new File("target/classes/"+packageName.replace('.', '/'));

        for (File file: Objects.requireNonNull(packageDirectory.listFiles())){
            if(file.isDirectory()){
                packageClasses.addAll(getClassesInPackage(packageName + "." + file.getName()));
            }else if(file.getName().contains(".class")){
                classNames.add(file.getName());
            }
        }

        URLClassLoader ucl = new URLClassLoader(new URL[]{new File("target/classes/").toURI().toURL()});

        for (String className:classNames){
            packageClasses.add(ucl.loadClass(packageName+"."+className.substring(0,className.length()-6)));
        }


        return packageClasses;
    }
}
