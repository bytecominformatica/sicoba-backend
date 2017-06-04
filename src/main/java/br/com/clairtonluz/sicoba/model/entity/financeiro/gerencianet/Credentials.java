package br.com.clairtonluz.sicoba.model.entity.financeiro.gerencianet;

import br.com.clairtonluz.sicoba.config.Environment;
import br.com.clairtonluz.sicoba.config.EnvironmentFactory;
import org.json.JSONObject;

/**
 * Created by clairton on 09/11/16.
 */
public class Credentials {

    private static Credentials instance;

    private final String clientId;
    private final String clientSecret;
    private final String notificationUrl;
    private final boolean sandbox;
    private final JSONObject options;


    public static synchronized Credentials getInstance() {
        if (instance == null) {
            instance = new Credentials();
        }
        return instance;
    }

    private Credentials() {
        Environment environment = EnvironmentFactory.create();
        this.clientId = environment.getGNClientId();
        this.clientSecret = environment.getGNClientSecret();
        this.notificationUrl = environment.getGNNotificationUrl();
        this.sandbox = environment.isGNSandbox();

        options = new JSONObject();
        options.put("client_id", getClientId());
        options.put("client_secret", getClientSecret());
        options.put("sandbox", isSandbox());
    }

    public JSONObject getOptions() {
        return options;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }
}
