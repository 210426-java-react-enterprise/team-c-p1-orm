package com.revature.assigments.p1;

import com.revature.assigments.p1.models.AppAccount;
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


        /*AppUser appUserOut = new AppUser("jane.doe","password","Jane","Doe","jane.doe@gmail.com");
        appUserOut.setId(1);

        if(!objectService.sendInstanceToDB(appUserOut)){
            System.out.println("The object COULDN'T be saved into DB");
        }else{
            System.out.println("The object was SAVED into DB");
        }*/



        objectService.bringInstanceFromDB(AppUser.class,1);


        connectionPool.closeConnections();

    }


    private static class bringInstanceFromDB extends AppUser {
    }
}
