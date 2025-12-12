package gal.usc.etse.sharecloud.http;

public class TokenManager {
    private static String accessToken;

    public static void setAccessToken(String token) {
        accessToken = token;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static void reset() {
        accessToken = null;
    }
}
