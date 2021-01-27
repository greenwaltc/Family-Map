package Model;

public class AuthToken {

    private String UserName, Token;

    public AuthToken() {}

    /**
     * Creates an authorization token model object.
     *
     * @param userName Specified username (non-empty)
     * @param token Unique authorization token (non-empty)
     */
    public AuthToken(String userName, String token) {
        UserName = userName;
        Token = token;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
