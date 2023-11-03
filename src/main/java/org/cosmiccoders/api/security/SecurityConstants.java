package org.cosmiccoders.api.security;

public class SecurityConstants {
    public static final long JWT_EXPIRATION = 1_800_000; //30 minutes in milliseconds = 1800000
    public static final long JWT_EXPIRATION_SECONDS = 1800; //30 minutes in seconds = 1800
    public static final String JWT_SECRET = "LFwXiQ4IHkW3dtcg3C6xWNvIVxGsljh4L9m2d7dIJFunkfDCui2eAj1U2n3RpWY";
    public static final long REFRESH_EXPIRATION = 604_800_000; // 1 week in milliseconds = 604800000
    public static final long REFRESH_EXPIRATION_SECONDS = 604800; // 1 week in seconds = 604800
    public static final String REFRESH_SECRET = "LFwXiQ4IHkW3dtcg3C6xWNvIVxGsljh4L3m2d7dIJFunkfDCui2eAj1U2n3RpWY";
    public static final String ISSUER = "127.0.0.1:8080";
    public static final boolean IS_COOKIE_SECURE = false;
    public static final boolean IS_PROD = false;
    public static final String COOKIE_DOMAIN = "localhost";
    public static final String COOKIE_SAME_SITE = "None";
    public static final String FRONTEND_URL = "http://localhost:3000";
    public static final String FRONTEND_INTERNAL_URL = "http://localhost:3000";
    public static final String BACKEND_URL = "http://localhost:8080";
}
