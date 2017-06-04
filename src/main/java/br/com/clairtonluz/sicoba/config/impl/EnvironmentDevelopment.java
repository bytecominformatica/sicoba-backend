package br.com.clairtonluz.sicoba.config.impl;

import br.com.clairtonluz.sicoba.config.Environment;

/**
 * Created by clairtonluz on 28/02/16.
 */
public class EnvironmentDevelopment extends Environment {
    @Override
    public String getEnv() {
        return Environment.DEVELOPMENT;
    }

    @Override
    public String getJWTSecret() {
        return "Your secret";
    }
}
