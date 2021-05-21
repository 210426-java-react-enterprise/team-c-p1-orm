package com.revature.assigments.p1;

import com.revature.assigments.p1.models.AppUser;
import com.revature.assigments.p1.repos.ClassDAO;
import com.revature.assigments.p1.repos.ConnectionsController;
import com.revature.assigments.p1.services.ClassService;
import com.revature.assigments.p1.services.ObjectMapper;

import java.util.*;

public class MyCustomORMDriver {

    public static void main(String[] args){

        ClassDAO classDAO = new ClassDAO();
        ConnectionsController connectionsController = new ConnectionsController();
        ClassService classService = new ClassService(classDAO, connectionsController);
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
        TreeMap<String,ArrayList<String>> instanceMapped;

        AppUser appUser = new AppUser("jane.doe","password","Jane","Doe","jane.doe@gmail.com");
        appUser.setId(1);
        //I need to include a method map the fields sequence to build the DB table
        objectMapped = (TreeMap<String, ArrayList<String>>) ObjectMapper.createObjetMapForDB(appUser);
        instanceMapped = (TreeMap<String, ArrayList<String>>) ObjectMapper.createInstanceMapForDB(appUser);



        //connectionsController.closeConnections();

    }


}
