package com.karta.battlepass.api.exception;

/** A base exception for all custom exceptions thrown by the KartaBattlePass API. */
public class KartaBattlePassException extends RuntimeException {

    public KartaBattlePassException(final String message) {
        super(message);
    }

    public KartaBattlePassException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
