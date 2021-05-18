package com.revature.assigments.p1.services;

import com.revature.assigments.p1.repos.ClassDAO;

public class ClassService {
    private ClassDAO classDao;

    public ClassService(ClassDAO classDao) {
        this.classDao = classDao;
    }

    public boolean saveClass(Class<?> newClassToBeSaved){
        classDao.saveClass(newClassToBeSaved);
        return true;
    }

}
