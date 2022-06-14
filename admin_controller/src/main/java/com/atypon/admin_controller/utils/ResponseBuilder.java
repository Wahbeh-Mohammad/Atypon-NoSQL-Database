package com.atypon.admin_controller.utils;

import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.models.layer_communication.Message;
import com.atypon.admin_controller.models.layer_communication.MessageStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {

    private ResponseBuilder() {}

    public static ResponseEntity<String> badRequest(String message) {
        return new ResponseEntity<>(new Message(MessageStatus.USER_ERROR,message).toString(), HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<String> fromMessage(AbstractMessage message) {
        HttpStatus status = httpStatusFromMessageStatus(message.getStatus());
        return new ResponseEntity<>(message.toJSON().toString(2), status);
    }

    private static HttpStatus httpStatusFromMessageStatus(MessageStatus messageStatus) {
        if(messageStatus.equals(MessageStatus.GOOD))
            return HttpStatus.ACCEPTED;
        else if(messageStatus.equals(MessageStatus.USER_ERROR))
            return HttpStatus.BAD_REQUEST;
        else
            return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
