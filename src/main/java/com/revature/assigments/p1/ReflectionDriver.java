package com.revature.assigments.p1;

import com.revature.assigments.p1.models.AppUser;
import com.revature.assigments.p1.util.Entity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ReflectionDriver {
    public static void main(String[] args){
        ReflectionDriver driver = new ReflectionDriver();
        try {
            driver.exploringClasses();
        } catch (ClassNotFoundException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        try{
            driver.exploringExternalClass();
        } catch (ClassNotFoundException | IllegalArgumentException | MalformedURLException e) {
            e.printStackTrace();
        }

        try{
            driver.exploringMyClasses();;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("+------------------------------------+");
        try{
            List<Class<?>> entityClasses = driver.getClassesInPackage("com.revature.assigments.p1.models");
            for(Class<?>  entity : entityClasses){
                System.out.println(entity);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("+------------------------------------+");
        try{
            String packageName = "com.revature.assigments.p1.models";
            List<Class<?>> entityClassesWithConstraints = driver.getClassesInPackageWithConstraints(packageName, clazz -> clazz.isAnnotationPresent(Entity.class));
            for(Class<?>  entityWithConstraints : entityClassesWithConstraints ){
                System.out.println(entityWithConstraints);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


    public void exploringClasses() throws ClassNotFoundException{
        Class<?> thisClass = ReflectionDriver.class;
        Class<?> sameClass = this.getClass();

        System.out.println("thisClass == this.getClass() >> "+ (thisClass == this.getClass()));

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        Class <?> someClass = currentClassLoader.loadClass("com.revature.assigments.p1.ReflectionDriver");

        System.out.println("thisClass == someClass >> "+ (thisClass == someClass));
        System.out.println("sameClass == someClass >> "+ (sameClass == someClass));

    }

    public void exploringExternalClass() throws ClassNotFoundException, IllegalArgumentException, MalformedURLException {
        URL[] urls = new URL[]{new File("target/classes").toURI().toURL()};
        URLClassLoader ucl = new URLClassLoader(urls);
        Class<?> anotherClass = ucl.loadClass("com.revature.assigments.p1.ReflectionDriver");
        System.out.println("this.getClass == anotherClass  >> " + (this.getClass()==anotherClass));
    }

    public void exploringMyClasses()throws ClassNotFoundException{
        Class<?> myClass = AppUser.class;
        System.out.println("My AppUser Class"+(myClass.getName()));
        System.out.println("My AppUser Class"+(myClass.getSimpleName()));
    }

    public List<Class<?>> getClassesInPackage(String packageName) throws MalformedURLException, ClassNotFoundException {
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

    public List<Class<?>> getClassesInPackageWithConstraints(String packageName, Predicate<Class<?>> predicate) throws MalformedURLException, ClassNotFoundException {
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


}
