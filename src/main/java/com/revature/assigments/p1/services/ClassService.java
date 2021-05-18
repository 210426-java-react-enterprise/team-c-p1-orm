package com.revature.assigments.p1.services;

import com.revature.assigments.p1.repos.ClassDAO;
import com.revature.assigments.p1.repos.ConnectionsController;

import java.sql.Connection;
import java.sql.SQLException;

public class ClassService {
    private ClassDAO classDao;
    private ConnectionsController connectionsController;


    public ClassService(ClassDAO classDao, ConnectionsController connectionsController) {
        this.classDao = classDao;
        this.connectionsController = connectionsController;
    }

    public boolean saveClass(Class<?> newClassToBeSaved) throws SQLException {
        Connection conn  = connectionsController.pollFromConnectionPool();
        classDao.saveClass(newClassToBeSaved, conn);

        connectionsController.addToConnectionPool(conn);
        return true;
    }

}
