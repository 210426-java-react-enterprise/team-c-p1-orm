package com.revature.assigments.orm;

import com.revature.assigments.orm.models.AppUser;
import com.revature.assigments.orm.repos.ObjectDAO;
import com.revature.assigments.orm.repos.ConnectionPool;
import com.revature.assigments.orm.services.ObjectService;
import com.revature.assigments.orm.util.PackagesReader;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

public class MyCustomORMDriver {

    public static void main(String[] args){

        ObjectDAO objectDAO = new ObjectDAO();
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        ObjectService objectService = new ObjectService(objectDAO, connectionPool);

//        try{
//            String packageName = "com.revature.assigments.p1.models";
//            List<Class<?>> entityClassesWithConstraints = PackagesReader.getClassesInPackageWithConstraints(packageName, clazz -> clazz.isAnnotationPresent(Entity.class));
//            for(Class<?>  entityWithConstraints : entityClassesWithConstraints ){
//                System.out.println(entityWithConstraints);
//                List<Annotation> annotationsList = Arrays.asList(new ObjectReader().readEntity(entityWithConstraints));
//                classService.saveClass(entityWithConstraints);
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }

//HERE IS FINE
//        AppUser appUserOut = new AppUser("john.doe","p4ssw0rd","John","Doe","john.doe@gmail.com");
//        appUserOut.setId(2);
//
//        if(!objectService.sendObjectToDB(appUserOut)){
//            System.out.println("The object COULDN'T be saved into DB");
//        }else{
//            System.out.println("The object was SAVED into DB");
//        }


        
        AppUser newAppUser = objectService.bringObjectFromDbByField(AppUser.class,"user_id", "2");
        System.out.println(newAppUser.toString());
        
        connectionPool.closeConnections();

    }


    private static class bringInstanceFromDB extends AppUser {
    }
}
