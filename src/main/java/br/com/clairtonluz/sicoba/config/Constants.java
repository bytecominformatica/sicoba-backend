package br.com.clairtonluz.sicoba.config;


/**
 * Created by clairton on 19/07/17.
 */
public final class Constants {
    public static final String BASIC_AUTH_PREFIX = "Basic";
    public static final String TOKEN_AUTH_PREFIX = "Bearer";
    public static final long EXPIRATION_TIME = 3_600_000; // 1 hora
    public static final String JWT_SECRET = EnvironmentFactory.create().getJWTSecret();
    public static final String ALLOW_ORIGIN = EnvironmentFactory.create().getAllowOrigins();

    private Constants() {
    }
}
