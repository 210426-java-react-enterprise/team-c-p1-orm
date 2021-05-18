package com.revature.assigments.p1;

import com.revature.assigments.p1.annotations.Entity;
import com.revature.assigments.p1.repos.ClassDAO;
import com.revature.assigments.p1.repos.ConnectionsController;
import com.revature.assigments.p1.services.ClassService;
import com.revature.assigments.p1.util.ClassReader;
import com.revature.assigments.p1.util.PackagesReader;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class MyCustomORMDriver {

    public static void main(String[] args){

        ClassDAO classDAO = new ClassDAO();
        ConnectionsController connectionsController = new ConnectionsController();
        ClassService classService = new ClassService(classDAO, connectionsController);

        try{
            String packageName = "com.revature.assigments.p1.models";
            List<Class<?>> entityClassesWithConstraints = PackagesReader.getClassesInPackageWithConstraints(packageName, clazz -> clazz.isAnnotationPresent(Entity.class));
            for(Class<?>  entityWithConstraints : entityClassesWithConstraints ){
                System.out.println(entityWithConstraints);
                List<Annotation> annotationsList = Arrays.asList(new ClassReader().readEntity(entityWithConstraints));
                classService.saveClass(entityWithConstraints);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


}
