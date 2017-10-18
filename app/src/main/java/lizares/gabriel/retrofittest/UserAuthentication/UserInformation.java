package lizares.gabriel.retrofittest.UserAuthentication;

import java.io.Serializable;

/**
 * Created by Parcival on 10/11/2017.
 */

public class UserInformation implements Serializable {
    private String username = null;
    private String authToken =null;

    public UserInformation() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
