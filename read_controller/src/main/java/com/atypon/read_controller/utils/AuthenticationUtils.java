package com.atypon.read_controller.utils;

import com.atypon.read_controller.authentication.TokenAuthentication;
import com.atypon.read_controller.models.types.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtils {
    TokenAuthentication authentication;

    @Autowired
    public AuthenticationUtils(TokenAuthentication authentication) {
        this.authentication = authentication;
    }

    public boolean isAuthorized(String authToken) {
        if(authToken == null)
            return false;

        JSONObject decryptedPayload = authentication.verify(authToken);
        return decryptedPayload != null;
    }

    public boolean isAuthorizedAdmin(String authToken) {
        if(authToken == null)
            return false;

        JSONObject decryptedPayload = authentication.verify(authToken);
        if(decryptedPayload == null)
            return false;
        return User.fromJSON(decryptedPayload).isAdmin();
    }
}
