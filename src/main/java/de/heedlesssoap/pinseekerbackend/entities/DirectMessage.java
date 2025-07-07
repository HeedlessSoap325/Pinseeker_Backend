package de.heedlesssoap.pinseekerbackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class DirectMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer direct_message_id;

    @Column(nullable = false)
    private String receiver_encrypted_message;

    @Column(nullable = false)
    @JsonProperty("receiver_encrypted_aes_key")
    private String receiver_encrypted_aes_key;

    @Column(nullable = false)
    private String sender_encrypted_message;

    @Column(nullable = false)
    @JsonProperty("sender_encrypted_aes_key")
    private String sender_encrypted_aes_key;

    @Column(nullable = false)
    private Date created_at;

    @ManyToOne
    @JoinTable(
            name = "user_direct_message_junction",
            joinColumns = @JoinColumn(name = "direct_message_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private ApplicationUser sender;

    @ManyToOne
    @JoinTable(
            name = "chat_direct_message_junction",
            joinColumns = @JoinColumn(name = "direct_message_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_id")
    )
    private Chat chat;

    public DirectMessage() {
        this.created_at = new Date();
        this.sender = new ApplicationUser();
        this.sender_encrypted_message = null;
        this.sender_encrypted_aes_key = null;
        this.receiver_encrypted_message = null;
        this.receiver_encrypted_aes_key = null;
        this.chat = new Chat();
    }

    public DirectMessage(String receiver_encrypted_message, String receiver_encrypted_aes_key, String sender_encrypted_message, String sender_encrypted_aes_key, Date created_at, ApplicationUser sender, Chat chat) {
        this.receiver_encrypted_message = receiver_encrypted_message;
        this.receiver_encrypted_aes_key = receiver_encrypted_aes_key;
        this.sender_encrypted_message = sender_encrypted_message;
        this.sender_encrypted_aes_key = sender_encrypted_aes_key;
        this.created_at = created_at;
        this.sender = sender;
        this.chat = chat;
    }

    public Integer getDirectMessageId() {
        return direct_message_id;
    }

    public void setDirectMessageId(Integer direct_message_id) {
        this.direct_message_id = direct_message_id;
    }

    public String getReceiverEncryptedMessage() {
        return receiver_encrypted_message;
    }

    public void setReceiverEncryptedMessage(String receiver_encrypted_message) {
        this.receiver_encrypted_message = receiver_encrypted_message;
    }

    public String getReceiverEncryptedAESKey() {
        return receiver_encrypted_aes_key;
    }

    public void setReceiverEncryptedAESKey(String receiver_encrypted_aes_key) {
        this.receiver_encrypted_aes_key = receiver_encrypted_aes_key;
    }

    public String getSenderEncryptedMessage() {
        return sender_encrypted_message;
    }

    public void setSenderEncryptedMessage(String sender_encrypted_message) {
        this.sender_encrypted_message = sender_encrypted_message;
    }

    public String getSenderEncryptedAESKey() {
        return sender_encrypted_aes_key;
    }

    public void setSenderEncryptedAESKey(String sender_encrypted_aes_key) {
        this.sender_encrypted_aes_key = sender_encrypted_aes_key;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }

    public ApplicationUser getSender() {
        return sender;
    }

    public void setSender(ApplicationUser sender) {
        this.sender = sender;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @Override
    public String toString() {
        return "DirectMessage{" +
                "direct_message_id=" + direct_message_id +
                ", receiver_encrypted_message='" + receiver_encrypted_message + '\'' +
                ", receiver_encrypted_aes_key='" + receiver_encrypted_aes_key + '\'' +
                ", sender_encrypted_message='" + sender_encrypted_message + '\'' +
                ", sender_encrypted_aes_key='" + sender_encrypted_aes_key + '\'' +
                ", created_at=" + created_at +
                ", sender=" + sender.toString() +
                ", chat_id=" + chat.toString() +
                '}';
    }
}