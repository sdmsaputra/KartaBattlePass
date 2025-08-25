package com.karta.battlepass.api.exception;

/**
 * An exception thrown when an economy operation fails.
 */
public class EconomyException extends KartaBattlePassException {

    public EconomyException(String message) {
        super(message);
    }

    public EconomyException(String message, Throwable cause) {
        super(message, cause);
    }
}
