package de.heedlesssoap.pinseekerbackend.entities;

import de.heedlesssoap.pinseekerbackend.entities.enums.ChatState;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chat_id;

    @ManyToMany
    @JoinTable(
            name = "chat_participant_junction",
            joinColumns = {@JoinColumn(name = "chat_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<ApplicationUser> participants;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DirectMessage> messages;

    @Enumerated(EnumType.STRING)
    private ChatState chat_state;

    public Chat(Set<ApplicationUser> participants, Set<DirectMessage> messages, ChatState chat_state) {
        this.participants = participants;
        this.messages = messages;
        this.chat_state = chat_state;
    }

    public Chat() {
        this.messages = new HashSet<>();
        this.participants = new HashSet<>();
        this.chat_state = ChatState.ACTIVE;
    }

    public Integer getChatId() {
        return chat_id;
    }

    public void setChatId(Integer chat_id) {
        this.chat_id = chat_id;
    }

    public Set<ApplicationUser> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<ApplicationUser> participants) {
        this.participants = participants;
    }

    public Set<DirectMessage> getMessages() {
        return messages;
    }

    public void setMessages(Set<DirectMessage> messages) {
        this.messages = messages;
    }

    public ChatState getChatState() {
        return chat_state;
    }

    public void setChatState(ChatState chat_state) {
        this.chat_state = chat_state;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "chat_id=" + chat_id +
                ", participants=" + participants +
                ", messages=" + messages +
                ", chat_state=" + chat_state +
                '}';
    }
}