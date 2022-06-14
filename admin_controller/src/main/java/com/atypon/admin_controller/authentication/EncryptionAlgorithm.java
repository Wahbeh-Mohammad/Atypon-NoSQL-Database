package com.atypon.admin_controller.authentication;

import com.atypon.admin_controller.models.layer_communication.AbstractMessage;

public interface EncryptionAlgorithm {
    AbstractMessage encrypt(String payload);
    AbstractMessage decrypt(String encryptedPayload);
}
