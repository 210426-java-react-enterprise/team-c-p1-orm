package com.revature.assigments.orm.repos;

import com.revature.assigments.orm.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class ConnectionPool{

    private Queue<Connection> connectionPool = new LinkedList<>();

    private static final ConnectionPool connectionPoolInstance = new ConnectionPool(System.getenv("num_conns"));

    private ConnectionPool(String num_connections) {
        this.connectionPool = ConnectionFactory.getInstance().getConnections(Integer.valueOf(num_connections));
    };

    public static ConnectionPool getConnectionPool(){
        return connectionPoolInstance;
    }

    public Connection pollFromConnectionPool() throws SQLException {
        Connection conn=null;

        if(connectionPool.peek()!=null){
            conn = connectionPool.poll();
        }else{
            throw new SQLException("No connection available!!!");
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
           } catch (SQLException throwables) {
               throwables.printStackTrace();
           }
       });
    }

    public int connectionsAvailable(){
        return connectionPool.stream().toArray().length;
    }

}
