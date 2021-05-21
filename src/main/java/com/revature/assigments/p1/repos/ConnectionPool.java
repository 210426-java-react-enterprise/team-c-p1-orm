package com.revature.assigments.p1.repos;

import com.revature.assigments.p1.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Predicate;

public class ConnectionPool{
    private final int NUMOFCONNECTIONS =2;
    private Queue<Connection> connections = new LinkedList<>(ConnectionFactory.getInstance().getPoolOfConnectionsAsQueue(NUMOFCONNECTIONS));

    public Connection pollFromConnectionPool() throws SQLException {
        Connection conn=null;

        if(connections.peek()!=null){
            conn = connections.poll();
        }else{
            throw new SQLException("No connection available!!!");
        }

        return conn;
    }

    public boolean addToConnectionPool(Connection conn){
        return connections.add(conn);
    }

    public void closeConnections(){
       connections.forEach( conn -> {
           try {
               conn.close();
           } catch (SQLException throwables) {
               throwables.printStackTrace();
           }
       });
    }

}
