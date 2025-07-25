package de.heedlesssoap.pinseekerbackend.entities.DTOs;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.Chat;
import de.heedlesssoap.pinseekerbackend.entities.enums.ChatState;

import java.util.Set;
import java.util.stream.Collectors;

public class GetChatDTO {
    private Integer chat_id;

    private ChatApplicationUserDTO participant;

    private Set<DirectMessageDTO> messages;

    private ChatState chat_state;

    public GetChatDTO() {
    }

    public GetChatDTO(Integer chat_id, ChatApplicationUserDTO participant, Set<DirectMessageDTO> messages, ChatState chat_state) {
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

    public ChatApplicationUserDTO getParticipant() {
        return participant;
    }

    public void setParticipant(ChatApplicationUserDTO participant) {
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

    public GetChatDTO fromChat(Chat chat, ApplicationUser otherUser) {
        this.setChatId(chat.getChatId());
        this.setParticipant(new ChatApplicationUserDTO().fromApplicationUser(otherUser));
        this.setMessages(chat.getMessages().stream().map(message ->
                new DirectMessageDTO().fromDirectMessage(message, message.getSender().equals(otherUser))).collect(Collectors.toSet())
        );
        this.setChatState(chat.getChatState());
        return this;
    }
}