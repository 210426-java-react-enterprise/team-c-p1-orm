package com.revature.assigments.p1.util;

import javax.sql.ConnectionPoolDataSource;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

//import static com.revature.assigments.p1.MyCustomORMDriver.NUMOFCONNECTIONS;

public class ConnectionFactory {

    private static ConnectionFactory connectionFactory; // Lazy Singleton
    private Properties props = new Properties();

    static{
        try{
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ConnectionFactory(){

        try{

        props.load(new FileReader("src/main/resources/connections/application.properties"));


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConnectionFactory getInstance() {

        if (connectionFactory == null){
            connectionFactory = new ConnectionFactory();
        }

        return connectionFactory;
    }

    public Queue<Connection> getConnections(int numOfConnections){
        Queue<Connection> conns = new LinkedList<>();
        Connection conn=null;
        try{
            for (int i = 0; i < numOfConnections; i++) {
                conn = DriverManager.getConnection(props.getProperty("host-url"),props.getProperty("username"), props.getProperty("password"));
                conns.add(conn);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conns;
    }


}
