package com.karta.battlepass.api.exception;

/**
 * An exception thrown when there is an error interacting with the data storage (e.g., database).
 */
public class StorageException extends KartaBattlePassException {

    public StorageException(final String message) {
        super(message);
    }

    public StorageException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
