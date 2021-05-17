package com.revature.assigments.p1;

import com.revature.assigments.p1.models.AppUser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ReflectionDriver {
    public static void main(String[] args) throws ClassNotFoundException {
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

}
