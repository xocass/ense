package gal.usc.etse.sharecloud.http;


public class TokenManager {
    private static String accessToken;
    private static String userID;
    private static String username;
    private static String image;

    public static void setAccessToken(String token) {
        accessToken = token;
    }
    public static void setUserID(String id) {userID = id;}
    public static void setUsername(String displayName) {username = displayName;}
    public static void setImage(String profilePic) {image = profilePic;}

    public static String getAccessToken() {
        return accessToken;
    }
    public static String getUserID() {return  userID;}
    public static String getUsername() {return username;}
    public static String getImage() {return image;}

    public static void reset() {
        accessToken = null; userID = null;
    }
}
