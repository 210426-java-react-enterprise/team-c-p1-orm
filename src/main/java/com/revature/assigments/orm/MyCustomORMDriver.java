package com.revature.assigments.orm;

import com.revature.assigments.orm.models.AppAccount;
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


        
//        AppUser newUser = objectService.bringObjectFromDbByField(AppUser.class,"user_id", "1");
//        System.out.println(newUser.toString());
//
//        newUser.setEmail("jane.doe@yahoo.com");
//        if(!objectService.updateObjectinDB(newUser)){
//            System.out.println("The object COULDN'T be updated into DB");
//        }else {
//            System.out.println("The object was updated into DB");
//        }
    
//        System.out.println("----------------------------------------------");
//
//        AppUser otherUser = new AppUser("alice.doe","passw0rd","Alice","Doe","alice@outlook.com");
//
//        otherUser.setId(3);
//
//        if(!objectService.sendObjectToDB(otherUser)){
//            System.out.println("The object COULDN'T be saved into DB");
//        }else{
//            System.out.println("The object was SAVED into DB");
//        }
//
//        System.out.println(otherUser.toString());
//
//        System.out.println("----------------------------------------------");
//
//        if(!objectService.deleteObjectInDB(otherUser)){
//            System.out.println("The object COULDN'T be delete into DB");
//        }else{
//            System.out.println("The object was delete it");
//        }
//
//        System.out.println("----------------------------------------------");
        
        AppAccount newAccount = new AppAccount(1,"Saving","USD",300,1);
        
        System.out.println(newAccount.toString());
        
        if(!objectService.sendObjectToDB(newAccount)){
            System.out.println("The object COULDN'T be saved into DB");
        }else{
            System.out.println("The object was SAVED into DB");
        }
        
        System.out.println("----------------------------------------------");
        
        connectionPool.closeConnections();
    }
    
    
    
}
