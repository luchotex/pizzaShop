/*
 * InvalidRequestException.java
 */

package com.tx.pizzashop.exception;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

/**
 *
 * @author Luis Kupferberg
 */
public class InvalidRequestException extends ClientErrorException {

    /**
     * Creates a new instance of InvalidRequestException.
     * @param message String
     */
    public InvalidRequestException(String message) {
        super(message, Response.Status.BAD_REQUEST);
    }

}
