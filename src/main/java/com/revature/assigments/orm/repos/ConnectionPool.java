package com.revature.assigments.orm.repos;

import com.revature.assigments.orm.exceptions.ConnetionNotAvailable;
import com.revature.assigments.orm.util.ConnectionFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

public class ConnectionPool{

    private Queue<Connection> connectionPool = new LinkedList<>();
    private Properties props = new Properties();
    private static final ConnectionPool connectionPoolInstance = new ConnectionPool();


    private ConnectionPool() {

        try{

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("application.properties");
            props.load(input);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.connectionPool = ConnectionFactory.getInstance().getConnections(props.getProperty("qty-connections"),
                                                                             props.getProperty("host-url"),
                                                                             props.getProperty("username"),

                                                                             props.getProperty("password"));
    }


    public static ConnectionPool getConnectionPool(){
        return connectionPoolInstance;
    }

    public Connection pollFromConnectionPool() throws ConnetionNotAvailable {
        Connection conn=null;

        if(connectionPool.peek()!=null){
            conn = connectionPool.poll();
        }else{
            throw new ConnetionNotAvailable ("No connection available!!!");
        }

        return conn;
    }

    public boolean addToConnectionPool(Connection conn){
        return connectionPool.add(conn);
    }

    public void closeConnections(){
       connectionPool.forEach( conn -> {
           try {
               conn.close();
           } catch (Exception throwables) {
               throwables.printStackTrace();
           }
       });
    }

    public int connectionsAvailable(){
        return connectionPool.stream().toArray().length;
    }

}
