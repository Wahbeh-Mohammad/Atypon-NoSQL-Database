package com.atypon.read_controller.authentication;

import com.atypon.read_controller.models.layer_communication.AbstractMessage;

public interface EncryptionAlgorithm {
    AbstractMessage encrypt(String payload);
    AbstractMessage decrypt(String encryptedPayload);
}
