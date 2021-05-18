package com.revature.assigments.p1;

import com.revature.assigments.p1.annotations.Entity;
import com.revature.assigments.p1.repos.ClassDAO;
import com.revature.assigments.p1.services.ClassService;
import com.revature.assigments.p1.util.PackagesReader;

import java.net.MalformedURLException;
import java.util.List;

public class MyCustomORMDriver {

    public static void main(String[] args){

        ClassDAO classDAO = new ClassDAO();
        ClassService classService = new ClassService(classDAO);

        try{
            String packageName = "com.revature.assigments.p1.models";
            List<Class<?>> entityClassesWithConstraints = PackagesReader.getClassesInPackageWithConstraints(packageName, clazz -> clazz.isAnnotationPresent(Entity.class));
            for(Class<?>  entityWithConstraints : entityClassesWithConstraints ){
                System.out.println(entityWithConstraints);
                classService.saveClass(entityWithConstraints);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


}
