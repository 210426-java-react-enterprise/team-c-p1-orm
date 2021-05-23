package com.revature.assigments.p1.services;

import com.revature.assigments.p1.repos.ObjectDAO;
import com.revature.assigments.p1.repos.ConnectionPool;
import com.revature.assigments.p1.util.ObjectMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;

public class ObjectService {
    private ObjectDAO objectDao;
    private ConnectionPool connectionPool;


    public ObjectService(ObjectDAO objectDao, ConnectionPool connectionPool) {
        this.objectDao = objectDao;
        this.connectionPool = connectionPool;
    }

    /**
     * This method takes the object to verify it has the annotation @Entity and @Table, and if it passes it calls
     * the correspondent DAO method to build the query and send it to DB
     *
     * @param object -- The object that is going to be sent to DB
     * @return -- Return a boolean TRUE (the obj was saved) FALSE (The obj wasn't save)
     */
    public boolean sendInstanceToDB(Object object) {

        TreeMap<String,ArrayList<String>> objectMapped;
        HashMap<String,ArrayList<String>> instanceMapped;
        ArrayList<String> objectMapSequence;
        Connection conn = null;

        //1.-Getting the connection from the pool
        try {
            conn = connectionPool.pollFromConnectionPool();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        //2.-Verifying and populating the support data structures
        try{
            ObjectMapper.verifyObjectClass(object);// Verifying if the object has the annotations @Entity and @Table
            objectMapSequence = (ArrayList<String>) ObjectMapper.objectFieldSequence(object);
            objectMapped = (TreeMap<String, ArrayList<String>>) ObjectMapper.createObjetMapForDB(object);
            instanceMapped = (HashMap<String, ArrayList<String>>) ObjectMapper.createInstanceMapForDB(object);

            //3.-Calling the DAO method
            if(!objectDao.saveInstance(conn, objectMapSequence, objectMapped, instanceMapped)){
                connectionPool.addToConnectionPool(conn);//Returning de conn to the pool
                return false;
            }

        }catch (RuntimeException e){
            System.out.println(e.getMessage());
        }

        connectionPool.addToConnectionPool(conn);//Returning the conn to the pool
        return true;
    }

    /**
     * This method is responsible to check the class file, get the conn and call the DAO method to populate
     * the new instance requested
     *
     * @param T -- The class
     * @param objectId -- The object Id to be able to read the DB and bring the info
     * @param <T>
     * @param <E>
     * @return -- The requested new instance of the class passed as input
     */
    public <T, E> T bringInstanceFromDB(Class<?> T, E objectId){
        Connection conn = null;
        Object object = makeNewInstance(T);


        try {
            conn = connectionPool.pollFromConnectionPool();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try{

            ObjectMapper.verifyObjectClass(object); // Verifying if the object has the annotations @Entity and @Table
            objectDao.requestInstanceData(object, objectId);



            return (T) object;

        }catch (RuntimeException e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * This method create a new instance from the input class and return it
     * @param clazz -- This is the Class utilize to create the new instance
     * @param <T>
     * @return -- Return the new Instance of the Class
     */
    public <T> T makeNewInstance(Class<T> clazz){
        try{
            Constructor<?> objectConstructor = clazz.getConstructor();
            Object object = Objects.requireNonNull(objectConstructor.newInstance());
            return (T) object;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

       return null;
    }

}
