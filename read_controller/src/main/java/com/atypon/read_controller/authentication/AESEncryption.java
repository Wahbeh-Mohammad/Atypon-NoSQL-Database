package com.atypon.read_controller.authentication;

import com.atypon.read_controller.models.layer_communication.AbstractMessage;
import com.atypon.read_controller.models.layer_communication.ContentMessage;
import com.atypon.read_controller.models.layer_communication.Message;
import com.atypon.read_controller.models.layer_communication.MessageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AESEncryption implements EncryptionAlgorithm {
    private final String ALGORITHM = "AES";
    private final SecretKey secretKey;

    @Autowired
    public AESEncryption(ApplicationContext context) {
        String secretKeyString = (String) context.getBean("secretKey");
        byte[] encodedKey = Base64.getDecoder().decode(secretKeyString);
        this.secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, ALGORITHM);
    }

    @Override
    public AbstractMessage encrypt(String payload) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(payload.getBytes());
            return new ContentMessage<>(MessageStatus.GOOD, "Encrypted", Base64.getEncoder().encodeToString(cipherText));
        } catch (Exception e) {
            return new Message(MessageStatus.BAD, e.getMessage());
        }
    }

    @Override
    public AbstractMessage decrypt(String encryptedPayload) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(encryptedPayload));
            return new ContentMessage<>(MessageStatus.GOOD, "Decrypted", new String(plainText));
        } catch (Exception e) {
            return new Message(MessageStatus.BAD, e.getMessage());
        }
    }
}
