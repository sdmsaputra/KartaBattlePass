package com.karta.battlepass.api.exception;

/** An exception thrown when an economy operation fails. */
public class EconomyException extends KartaBattlePassException {

    public EconomyException(final String message) {
        super(message);
    }

    public EconomyException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
