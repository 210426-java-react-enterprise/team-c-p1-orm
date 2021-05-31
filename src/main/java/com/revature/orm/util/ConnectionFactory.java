package com.revature.orm.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;


public class ConnectionFactory {

    private static ConnectionFactory connectionFactory = new ConnectionFactory();

    static{
        try{
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ConnectionFactory(){ }

    public static ConnectionFactory getInstance() {

        return connectionFactory;
    }

    public Queue<Connection> getConnections(String qtyOfConnections, String hostUrl, String dbUsername, String dbPassword){
        Queue<Connection> conns = new LinkedList<>();
        Connection conn=null;
        try{
            for (int i = 0; i < Integer.parseInt(qtyOfConnections); i++) {
                conn = DriverManager.getConnection(hostUrl,dbUsername,dbPassword);
                conns.add(conn);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conns;
    }

}
