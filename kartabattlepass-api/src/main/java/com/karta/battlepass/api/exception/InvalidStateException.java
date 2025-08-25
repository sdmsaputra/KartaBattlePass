package com.karta.battlepass.api.exception;

/**
 * An exception thrown when an operation is attempted in an invalid state. For example, trying to
 * claim a reward that has already been claimed.
 */
public class InvalidStateException extends KartaBattlePassException {

    public InvalidStateException(final String message) {
        super(message);
    }

    public InvalidStateException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
