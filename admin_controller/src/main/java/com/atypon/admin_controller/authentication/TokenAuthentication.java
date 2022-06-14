package com.atypon.admin_controller.authentication;

import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.models.layer_communication.ContentMessage;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@Component
public class TokenAuthentication {
    private final AESEncryption AES;
    private final int TOKEN_EXPIRY_TIME_IN_SECONDS = 7200;

    public TokenAuthentication() {
        this.AES = AESEncryption.getInstance();
    }

    public String sign(JSONObject payload) {
        // The tokens consist of 2 parts: {user credentials which is the payload}.{expiry date}
        Date expiryDate = Date.from(Instant.now().plusSeconds(TOKEN_EXPIRY_TIME_IN_SECONDS));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        String formattedExpiryDate = dateFormat.format(expiryDate);

        String payloadAsString = payload.toString();

        AbstractMessage payloadEncryptionMessage = AES.encrypt(payloadAsString);
        if( !payloadEncryptionMessage.isGood() )
            return null;
        String encryptedPayload = ((ContentMessage<String>) payloadEncryptionMessage).getContent();

        AbstractMessage encryptedDateMessage = AES.encrypt(formattedExpiryDate);
        if( !encryptedDateMessage.isGood() )
            return null;
        String encryptedExpiryDate = ((ContentMessage<String>) encryptedDateMessage).getContent();

        return encryptedPayload+"."+encryptedExpiryDate;
    }

    public JSONObject verify(String token) {
        String[] halvedToken = token.split("\\.");
        String encryptedPayload    = halvedToken[0],
               encryptedExpiryDate = halvedToken[1];

        AbstractMessage dateDecryptionMessage = AES.decrypt(encryptedExpiryDate);
        if(!dateDecryptionMessage.isGood())
            return null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        String expiryDateDecryption = ((ContentMessage<String>)dateDecryptionMessage).getContent();
        try {
            Date expiresAt = dateFormat.parse(expiryDateDecryption);
            // check if the current date is less than the issue date.
            // currentDate <= expiryDate ? authorized : not authorized
            if(Instant.now().isAfter(expiresAt.toInstant()))
                return null;
        } catch (ParseException e) { // invalid issue date, not authorized.
            return null;
        }

        AbstractMessage payloadDecryptionMessage = AES.decrypt(encryptedPayload);
        if(!payloadDecryptionMessage.isGood())
            return null;

        String payload = ((ContentMessage<String>) payloadDecryptionMessage).getContent();
        return new JSONObject(payload);
    }
}