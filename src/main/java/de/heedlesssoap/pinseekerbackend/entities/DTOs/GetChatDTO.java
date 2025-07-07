package de.heedlesssoap.pinseekerbackend.entities.DTOs;

import de.heedlesssoap.pinseekerbackend.entities.enums.ChatState;

import java.util.Set;

public class GetChatDTO {
    private Integer chat_id;

    private ApplicationUserDTO participant;

    private Set<DirectMessageDTO> messages;

    private ChatState chat_state;

    public GetChatDTO() {
    }

    public GetChatDTO(Integer chat_id, ApplicationUserDTO participant, Set<DirectMessageDTO> messages, ChatState chat_state) {
        this.chat_id = chat_id;
        this.participant = participant;
        this.messages = messages;
        this.chat_state = chat_state;
    }

    public Integer getChatId() {
        return chat_id;
    }

    public void setChatId(Integer chat_id) {
        this.chat_id = chat_id;
    }

    public ApplicationUserDTO getParticipant() {
        return participant;
    }

    public void setParticipant(ApplicationUserDTO participant) {
        this.participant = participant;
    }

    public Set<DirectMessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(Set<DirectMessageDTO> messages) {
        this.messages = messages;
    }

    public ChatState getChatState() {
        return chat_state;
    }

    public void setChatState(ChatState chat_state) {
        this.chat_state = chat_state;
    }
}