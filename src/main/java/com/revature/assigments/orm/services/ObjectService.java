package com.revature.assigments.orm.services;

import com.revature.assigments.orm.exceptions.ConnetionNotAvailable;
import com.revature.assigments.orm.exceptions.ObjectNotFoundInDB;
import com.revature.assigments.orm.repos.ObjectDAO;
import com.revature.assigments.orm.repos.ConnectionPool;
import com.revature.assigments.orm.util.ObjectMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

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
    @SuppressWarnings("unchecked")
    public boolean sendObjectToDB(Object object) {

        HashMap<String,ArrayList<String>> classMap;
        HashMap<String,ArrayList<String>> objectMap;
        ArrayList<String> fieldSequence;
        Connection conn = null;

        //1.-Getting the connection from the pool
        try {
            conn = connectionPool.pollFromConnectionPool();
        } catch (ConnetionNotAvailable e){
            System.out.println(e.getMessage());
        }

        //2.-Verifying and populating the support data structures
        try{
            ObjectMapper.verifyObjectClass(object);// Verifying if the object has the annotations @Entity and @Table
            fieldSequence = (ArrayList<String>) ObjectMapper.createFieldSequence(object);
            classMap = (HashMap<String, ArrayList<String>>)  ObjectMapper.createClassMap(object);
            objectMap = (HashMap<String, ArrayList<String>>) ObjectMapper.createObjectMap(object);

            //3.-Calling the DAO method
            if(!objectDao.saveObject(conn, fieldSequence, classMap, objectMap)){
                connectionPool.addToConnectionPool(conn);//Returning de conn to the pool
                return false;
            }

        }catch (RuntimeException e){
            System.out.println(e.getMessage());
        }

        connectionPool.addToConnectionPool(conn);//Returning the conn to the pool
        return true;
    }
    
    public boolean updateObjectinDB(Object object){
        HashMap<String,ArrayList<String>> classMap;
        HashMap<String,ArrayList<String>> objectMap;
        ArrayList<String> fieldSequence;
        Connection conn = null;
    
        //1.-Getting the connection from the pool
        try {
            conn = connectionPool.pollFromConnectionPool();
        } catch (ConnetionNotAvailable e){
            System.out.println(e.getMessage());
        }
    
        //2.-Verifying and populating the support data structures
        try{
            ObjectMapper.verifyObjectClass(object);// Verifying if the object has the annotations @Entity and @Table
            fieldSequence = (ArrayList<String>) ObjectMapper.createFieldSequence(object);
            classMap = (HashMap<String, ArrayList<String>>)  ObjectMapper.createClassMap(object);
            objectMap = (HashMap<String, ArrayList<String>>) ObjectMapper.createObjectMap(object);
        
            //3.-Calling the DAO method
            if(!objectDao.updateObject(conn, fieldSequence, classMap, objectMap)){
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
     * @param objectField -- The object Field to be able to filter the read of DB and bring the info
     * @param objectFieldValue -- The object field value to apply the filter
     * @param <T>
     * @param <E>
     * @return -- The requested new instance of the class passed as input
     */
    @SuppressWarnings("unchecked")
    public <T,E> T bringObjectFromDbByField(Class<?> T, E objectField, E objectFieldValue){
        Connection conn = null;
        Class<T> clazz = (Class<T>) T;
        T object = (T) makeNewInstance(T); // Here I create the new instance for the object
        
        HashMap<String,String> objectFieldsValuesRequestedFromDB;
        ArrayList<String> fieldSequence;
        HashMap<String, String> columnFieldMap;
        HashMap<String,ArrayList<String>> classMap;
     
        
        //1.-Getting the connection from the pool
        try {
            conn = connectionPool.pollFromConnectionPool();
        } catch (ConnetionNotAvailable e) {
            System.out.println(e.getMessage());
        }
        //2.-Verifying and populating the support data structures
        try{
            ObjectMapper.verifyObjectClass(object); // Verifying if the object has the annotations @Entity and @Table
    
            fieldSequence = (ArrayList<String>) ObjectMapper.createFieldSequence(object);
            columnFieldMap = (HashMap<String, String>) ObjectMapper.createColumnFieldMap(object);
            classMap = (HashMap<String, ArrayList<String>>) ObjectMapper.createClassMap(object);
            //3.-Calling the DAO method to populate the Object Requested Map
    
            objectFieldsValuesRequestedFromDB = (HashMap<String, String>) objectDao.requestObjectDataByField(conn,
                                                                                                            object,
                                                                                                            objectField,
                                                                                                            objectFieldValue,
                                                                                                            classMap);

            //4.-Call the Object Mapper to populate the new Instance.O
            T Object = (T) ObjectMapper.updateNewInstance(object, columnFieldMap, classMap, objectFieldsValuesRequestedFromDB);
            
            return (T) object;

        }catch(ObjectNotFoundInDB e){
            System.out.println(e.getMessage());
        }catch (RuntimeException e2){
            e2.getStackTrace();
        }

        return null;
    }

    /**
     * This method create a new instance from the input class and return it
     * @param clazz -- This is the Class utilize to create the new instance
     * @return -- Return the new Instance of the Class
     */
    @SuppressWarnings("unchecked")
    public <T> T makeNewInstance(Class<T> clazz){
        try{
            Constructor<?> objectConstructor = clazz.getConstructor();
            Object object = Objects.requireNonNull(objectConstructor.newInstance());
            return (T) object;
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

}
