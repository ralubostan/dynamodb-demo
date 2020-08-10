package com.softvision.dynamodb.rest.exceptions;

import org.springframework.core.NestedRuntimeException;

/**
 * Custom exception used for any persistence errors that have occurred.
 */
public class DynamoDBException extends NestedRuntimeException {

    public DynamoDBException(String msg) {
        super(msg);
    }

    public DynamoDBException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
