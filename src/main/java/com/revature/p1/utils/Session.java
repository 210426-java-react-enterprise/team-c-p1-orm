package com.revature.p1.utils;

import com.revature.p1.repos.DataSource;
import com.revature.p1.utils.annotations.Column;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Objects;

public class Session {

    private final EntityManager entityManager;

    public Session() {
        QueryBuilder queryBuilder = new QueryBuilder();
        entityManager = new EntityManager(queryBuilder);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }


//    public void shutdown() {
//        try {
//            //dataSource.shutdown();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

}

