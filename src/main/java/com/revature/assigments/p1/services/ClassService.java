package com.revature.assigments.p1.services;

import com.revature.assigments.p1.repos.ClassDAO;
import com.revature.assigments.p1.repos.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

public class ClassService {
    private ClassDAO classDao;
    private ConnectionPool connectionPool;


    public ClassService(ClassDAO classDao, ConnectionPool connectionPool) {
        this.classDao = classDao;
        this.connectionPool = connectionPool;
    }

    
    public boolean sendInstanceToDB(ArrayList<String> objectMapSequence, TreeMap<String, ArrayList<String>> objectMapped, TreeMap<String, ArrayList<String>> instanceMapped) {
        
        Connection conn = null;

        try {
            conn = connectionPool.pollFromConnectionPool();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        classDao.saveInstance(conn, objectMapSequence, objectMapped, instanceMapped);


        //connectionPool.addToConnectionPool();
        //return true;


        return false;
    }
}
