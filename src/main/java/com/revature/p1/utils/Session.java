package com.revature.p1.utils;

import com.revature.p1.repos.DataSource;
import com.revature.p1.utils.annotations.Column;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Objects;

public class Session {

    private DataSource dataSource;
    private QueryBuilder queryBuilder;
    private EntityManager entityManager;

    public Session() {
        dataSource = new DataSource();
        queryBuilder = new QueryBuilder();
        entityManager = new EntityManager(dataSource, queryBuilder);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

//    public void shutdown() {
//        try {
//            //dataSource.shutdown();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

}

