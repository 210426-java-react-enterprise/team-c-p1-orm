package com.revature.assigments.p1.repos;

import com.revature.assigments.p1.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Predicate;

public class ConnectionsController {
    private final int NUMOFCONNECTIONS =2;
    private Queue<Connection> connectionsPool = new LinkedList<>(ConnectionFactory.getInstance().getPoolOfConnectionsAsQueue(NUMOFCONNECTIONS));

    public Connection pollFromConnectionPool() throws SQLException {
        Connection conn=null;

        if(connectionsPool.peek()!=null){
            conn = connectionsPool.poll();
        }else{
            throw new SQLException("No connection available!!!");
        }

        return conn;
    }

    public boolean addToConnectionPool(Connection conn){
        return connectionsPool.add(conn);
    }

}
