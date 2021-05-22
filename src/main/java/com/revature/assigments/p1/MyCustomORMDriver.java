package com.revature.assigments.p1;

import com.revature.assigments.p1.models.AppUser;
import com.revature.assigments.p1.repos.ObjectDAO;
import com.revature.assigments.p1.repos.ConnectionPool;
import com.revature.assigments.p1.services.ObjectService;
import com.revature.assigments.p1.util.ObjectMapper;

import java.util.*;

public class MyCustomORMDriver {

    public static void main(String[] args){

        ObjectDAO objectDAO = new ObjectDAO();
        ConnectionPool connectionPool = ConnectionPool.getConnectionPool();
        ObjectService objectService = new ObjectService(objectDAO, connectionPool);
        /*
        try{
            String packageName = "com.revature.assigments.p1.models";
            List<Class<?>> entityClassesWithConstraints = PackagesReader.getClassesInPackageWithConstraints(packageName, clazz -> clazz.isAnnotationPresent(Entity.class));
            for(Class<?>  entityWithConstraints : entityClassesWithConstraints ){
                System.out.println(entityWithConstraints);
                List<Annotation> annotationsList = Arrays.asList(new ObjectReader().readEntity(entityWithConstraints));
                classService.saveClass(entityWithConstraints);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        */

        TreeMap<String,ArrayList<String>> objectMapped;
        HashMap<String,ArrayList<String>> instanceMapped;
        ArrayList<String> objectMapSequence;

        AppUser appUser = new AppUser("jane.doe","password","Jane","Doe","jane.doe@gmail.com");
        appUser.setId(1);

        objectMapSequence = (ArrayList<String>) ObjectMapper.objectFieldSequence(appUser);
        objectMapped = (TreeMap<String, ArrayList<String>>) ObjectMapper.createObjetMapForDB(appUser);
        instanceMapped = (HashMap<String, ArrayList<String>>) ObjectMapper.createInstanceMapForDB(appUser);
        if(!objectService.sendInstanceToDB(objectMapSequence,objectMapped,instanceMapped)){
            System.out.println("The object COULDN'T be saved into DB");
        }else{
            System.out.println("The object was SAVED into DB");
        }

        connectionPool.closeConnections();

    }


}
