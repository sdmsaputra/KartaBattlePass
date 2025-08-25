package com.karta.battlepass.api.exception;

/**
 * A base exception for all custom exceptions thrown by the KartaBattlePass API.
 */
public class KartaBattlePassException extends RuntimeException {

    public KartaBattlePassException(String message) {
        super(message);
    }

    public KartaBattlePassException(String message, Throwable cause) {
        super(message, cause);
    }
}
