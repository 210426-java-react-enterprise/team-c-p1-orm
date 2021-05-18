package com.revature.assigments.p1.repos;

import com.revature.assigments.p1.util.ConnectionFactory;

import java.sql.Connection;
import java.util.List;

public class ClassDAO {

    public void saveClass(Class<?> newClassToBeSaved){
        List<Connection> poolOfConnection = ConnectionFactory.getInstance().getPoolOfConnections(1);

    }


}
