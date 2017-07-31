package br.com.clairtonluz.sicoba.exception;

/**
 * Created by clairtonluz<clairton.c.l@gmail.com> on 02/04/16.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Exception e) {
        super(message, e);
    }
}
