package org.example;

public class RestaurantException extends Exception {
    public RestaurantException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestaurantException(String message) {
        super(message);
    }
}
