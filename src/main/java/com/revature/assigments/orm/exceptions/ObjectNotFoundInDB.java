package com.revature.assigments.orm.exceptions;

import java.sql.SQLException;

public class ObjectNotFoundInDB extends SQLException {
    public ObjectNotFoundInDB (String message){
        super(message);
    }
}
