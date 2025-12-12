package gal.usc.etse.sharecloud.http;

public class TokenManager {
    private static String accessToken;
    private static String userID;

    public static void setAccessToken(String token) {
        accessToken = token;
    }
    public static void setUserID(String id) {userID = id;}

    public static String getAccessToken() {
        return accessToken;
    }
    public static String getUserID() {return  userID;}

    public static void reset() {
        accessToken = null; userID = null;
    }
}
