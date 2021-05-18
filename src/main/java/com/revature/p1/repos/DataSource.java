package com.revature.p1.repos;

import com.revature.p1.repos.ConnectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataSource implements ConnectionPool {

    private List<Connection> connectionPool;
    private List<Connection> usedConnections = new ArrayList<>();
    private final int POOL_SIZE = 10;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public DataSource () {
        connectionPool = new ArrayList<>(POOL_SIZE);
        try {
            for (int i = 0; i < POOL_SIZE; i++){
                connectionPool.add(createConnection());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Connection getConnection() throws SQLException {
        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < POOL_SIZE) {
                connectionPool.add(createConnection());
            } else {
                throw new RuntimeException(
                        "Maximum pool size reached, no available connections!");
            }
        }

        Connection connection = connectionPool.remove(connectionPool.size() - 1);

        if(!connection.isValid(1)) {
            connection = createConnection();
        }

        usedConnections.add(connection);
        return connection;
    }

    @Override
    public boolean releaseConnection(Connection connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(
                System.getenv("host-url"),
                System.getenv("login"),
                System.getenv("password")
        );
    }

    public int getSize() {
        return connectionPool.size() + usedConnections.size();
    }

    //TODO make this work without throwing a ConcurrentModificationException
    /*
    public void shutdown() throws SQLException {
        usedConnections.forEach(this::releaseConnection);

        for (Connection c: connectionPool) {
            c.close();
        }
        connectionPool.clear();
    } */
}