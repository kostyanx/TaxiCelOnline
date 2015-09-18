/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline;

/**
 *
 * @author kostyanx
 */
public class TaxiInfoException extends Exception{

    public TaxiInfoException() {
    }

    public TaxiInfoException(String message) {
        super(message);
    }

    public TaxiInfoException(Throwable cause) {
        super(cause);
    }

    public TaxiInfoException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
