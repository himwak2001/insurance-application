package com.insurance.common.exception;

public class ResourceAlreadyExistException extends RuntimeException{
    public ResourceAlreadyExistException(String resourceName, String fieldName, String fieldValue){
        super(String.format("%s with %s '%s' already exists.", resourceName, fieldName, fieldValue));
    }
}
