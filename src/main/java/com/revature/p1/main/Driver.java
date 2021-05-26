package com.revature.p1.main;

import com.revature.p1.main.models.User;
import com.revature.p1.utils.EntityManager;
import com.revature.p1.utils.QueryBuilder;
import java.util.List;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Driver {

    private static Properties props = new Properties();

    public static void main(String[] args) {

        /*
            single quotes op, pls nerf :(
         */
//        try {
//            Class.forName("org.postgresql.Driver");
////            ClassLoader loader = Thread.currentThread().getContextClassLoader();
////            InputStream input = loader.getResourceAsStream("application.properties");
////            props.load(input);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        EntityManager em = new EntityManager();
//        List<User> users = em.getAllOnCondition(User.class, "password", "password");
//
//        users.forEach(System.out::println);


    }
}

