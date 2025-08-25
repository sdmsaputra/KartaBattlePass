package com.karta.battlepass.api.exception;

/**
 * An exception thrown when there is an error interacting with the data storage (e.g., database).
 */
public class StorageException extends KartaBattlePassException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
