package com.atypon.admin_controller.utils;

import com.atypon.admin_controller.authentication.TokenAuthentication;
import com.atypon.admin_controller.models.types.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtils {
    private final TokenAuthentication authentication;

    @Autowired
    public AuthenticationUtils(TokenAuthentication authentication) {
        this.authentication = authentication;
    }

    public boolean isAuthorizedAdmin(String authToken) {
        JSONObject decryptedPayload = authentication.verify(authToken);
        if(decryptedPayload == null)
            return false;
        return User.fromJSON(decryptedPayload).isAdmin();
    }
}
