package de.heedlesssoap.pinseekerbackend.entities.DTOs;

import de.heedlesssoap.pinseekerbackend.entities.DirectMessage;

import java.util.Date;

public class DirectMessageDTO {

    private Integer direct_message_id;

    private String encrypted_message;

    private String encrypted_aes_key;

    private Date created_at;

    private Boolean was_sender_other_user;

    public DirectMessageDTO() {
    }

    public DirectMessageDTO(Integer direct_message_id, String encrypted_message, String encrypted_aes_key, Date created_at, Boolean was_sender_other_user) {
        this.direct_message_id = direct_message_id;
        this.encrypted_message = encrypted_message;
        this.encrypted_aes_key = encrypted_aes_key;
        this.created_at = created_at;
        this.was_sender_other_user = was_sender_other_user;
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

    public String getEncryptedAesKey() {
        return encrypted_aes_key;
    }

    public void setEncryptedAesKey(String encrypted_aes_key) {
        this.encrypted_aes_key = encrypted_aes_key;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }

    public Boolean getWasSenderOtherUser() {
        return was_sender_other_user;
    }

    public void setWasSenderOtherUser(Boolean was_sender_other_user) {
        this.was_sender_other_user = was_sender_other_user;
    }

    public DirectMessageDTO fromDirectMessage(DirectMessage directMessage, Boolean was_sender_other_user) {
        this.direct_message_id = directMessage.getDirectMessageId();
        this.created_at = directMessage.getCreatedAt();
        this.was_sender_other_user = was_sender_other_user;

        if (!was_sender_other_user) {
            this.setEncryptedMessage(directMessage.getSenderEncryptedMessage());
            this.setEncryptedAesKey(directMessage.getSenderEncryptedAesKey());
        }else{
            this.setEncryptedMessage(directMessage.getReceiverEncryptedMessage());
            this.setEncryptedAesKey(directMessage.getReceiverEncryptedAesKey());
        }
        return this;
    }
}