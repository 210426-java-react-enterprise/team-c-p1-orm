package com.revature.assigments.p1.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import static com.revature.assigments.p1.MyCustomORMDriver.NUMOFCONNECTIONS;

public class ConnectionFactory {

    private static ConnectionFactory connectionFactory; // Lazy Singleton
    private List<Properties> propsPool = new ArrayList<>();

    static{
        try{
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ConnectionFactory(int numOfConnections){
        Properties props = new Properties();
        try{
            for (int i = 0; i < numOfConnections; i++) {
                String filePath = "src/main/resources/connections/application_thread_"+ i +".properties";
                props.load(new FileReader(filePath));
                propsPool.add(props);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConnectionFactory getInstance() {

        if (connectionFactory == null){
            connectionFactory = new ConnectionFactory(NUMOFCONNECTIONS);
        }

        return connectionFactory;
    }

    /*
    public Connection getConnection(){
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(props.getProperty("host=url"),props.getProperty("username"), props.getProperty("password"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }
    */

    public List<Connection> getPoolOfConnections(int numOfConnections){
        List<Connection> conns = new ArrayList<>();
        Connection conn = null;
        try{

            for(Properties props : propsPool){
                conn = DriverManager.getConnection(props.getProperty("host-url"),props.getProperty("username"), props.getProperty("password"));
                conns.add(conn);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return conns;
    }

}
