package com.revature.assigments.orm.exceptions;

import java.sql.SQLException;

public class ObjectNotFoundInDB extends Exception{
    public ObjectNotFoundInDB (String message){
        super(message);
    }
}
