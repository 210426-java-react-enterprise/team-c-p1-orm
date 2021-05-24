package com.revature.assigments.p1.exceptions;

import java.sql.SQLException;

public class ObjectNotFoundInDB extends SQLException {
    public ObjectNotFoundInDB (String message){
        super(message);
    }
}
