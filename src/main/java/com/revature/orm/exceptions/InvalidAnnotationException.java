package com.revature.orm.exceptions;

public class InvalidAnnotationException extends RuntimeException
{
    public InvalidAnnotationException(String message)
    {
        super(message);
    }
}
