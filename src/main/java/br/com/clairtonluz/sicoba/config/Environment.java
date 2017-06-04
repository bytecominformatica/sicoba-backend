package br.com.clairtonluz.sicoba.config;

import br.com.clairtonluz.sicoba.util.StringUtil;

/**
 * Created by clairtonluz on 28/02/16.
 */
public abstract class Environment {
    public static final String PRODUCTION = "PRODUCTION";
    public static final String DEVELOPMENT = "DEVELOPMENT";
    public static final String QUALITY = "QUALITY";

    public abstract String getEnv();


    public String getEmailSuporte() {
        return System.getenv("SICOBA_EMAIL_SUPORTE");
    }

    public String getEmailAdmin() {
        return System.getenv("SICOBA_EMAIL_ADMIN");
    }

    public String getJWTSecret() {
        return System.getenv("JWT_SECRET");
    }

    public String getSendgridApiKey() {
        return System.getenv("SENDGRID_API_KEY");
    }


    public String getGNClientId() {
        return System.getenv("CLIENT_ID");
    }

    public String getGNClientSecret() {
        return System.getenv("CLIENT_SECRET");
    }

    public String getGNNotificationUrl() {
        String notificationUrl = System.getenv("NOTIFICATION_URL");
        return StringUtil.isEmpty(notificationUrl) ? null : notificationUrl;
    }

    public boolean isGNSandbox() {
        return Boolean.parseBoolean(System.getenv("SANDBOX"));
    }
}
