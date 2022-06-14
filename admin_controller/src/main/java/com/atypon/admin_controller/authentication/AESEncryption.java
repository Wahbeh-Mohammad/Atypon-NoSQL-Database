package com.atypon.admin_controller.authentication;

import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.models.layer_communication.ContentMessage;
import com.atypon.admin_controller.models.layer_communication.Message;
import com.atypon.admin_controller.models.layer_communication.MessageStatus;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class AESEncryption implements EncryptionAlgorithm {
    private final String ALGORITHM = "AES";
    private static SecretKey secretKey;

    private static volatile AESEncryption INSTANCE = null;

    private AESEncryption() {
        try {
            secretKey = generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AESEncryption getInstance() {
        if(INSTANCE == null) { // singleton since secret key is dynamic.
            synchronized (AESEncryption.class) {
                if(INSTANCE == null)
                    INSTANCE = new AESEncryption();
            }
        }
        return INSTANCE;
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

    private SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }
}
