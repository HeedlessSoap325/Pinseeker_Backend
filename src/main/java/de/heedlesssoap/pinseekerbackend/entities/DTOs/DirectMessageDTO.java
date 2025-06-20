package de.heedlesssoap.pinseekerbackend.entities.DTOs;

import java.util.Date;

public class DirectMessageDTO {

    private Integer direct_message_id;

    private String encrypted_message;

    private String encrypted_aes_key;

    private Date created_at;

    private Integer sender_id;

    public DirectMessageDTO() {
    }

    public DirectMessageDTO(Integer direct_message_id, String encrypted_message, String encrypted_aes_key, Date created_at, Integer sender_id) {
        this.direct_message_id = direct_message_id;
        this.encrypted_message = encrypted_message;
        this.encrypted_aes_key = encrypted_aes_key;
        this.created_at = created_at;
        this.sender_id = sender_id;
    }

    public String getEncryptedMessage() {
        return encrypted_message;
    }

    public void setEncryptedMessage(String encrypted_message) {
        this.encrypted_message = encrypted_message;
    }

    public Integer getDirectMessageId() {
        return direct_message_id;
    }

    public void setDirectMessageId(Integer direct_message_id) {
        this.direct_message_id = direct_message_id;
    }

    public String getEncryptedAESKey() {
        return encrypted_aes_key;
    }

    public void setEncryptedAESKey(String encrypted_aes_key) {
        this.encrypted_aes_key = encrypted_aes_key;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }

    public Integer getSenderId() {
        return sender_id;
    }

    public void setSenderId(Integer sender_id) {
        this.sender_id = sender_id;
    }
}