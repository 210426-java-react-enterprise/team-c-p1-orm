package com.revature.assigments.p1.services;

import com.revature.assigments.p1.repos.ObjectDAO;
import com.revature.assigments.p1.repos.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class ObjectService {
    private ObjectDAO objectDao;
    private ConnectionPool connectionPool;


    public ObjectService(ObjectDAO objectDao, ConnectionPool connectionPool) {
        this.objectDao = objectDao;
        this.connectionPool = connectionPool;
    }

    
    public boolean sendInstanceToDB(ArrayList<String> objectMapSequence, TreeMap<String, ArrayList<String>> objectMapped, HashMap<String, ArrayList<String>> instanceMapped) {
        
        Connection conn = null;

        try {
            conn = connectionPool.pollFromConnectionPool();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        if(!objectDao.saveInstance(conn, objectMapSequence, objectMapped, instanceMapped)){
            connectionPool.addToConnectionPool(conn);
            return false;
        }

        connectionPool.addToConnectionPool(conn);
        return true;
    }


}
