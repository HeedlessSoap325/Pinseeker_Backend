package de.heedlesssoap.pinseekerbackend.services;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.Chat;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.ApplicationUserDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.DirectMessageDTO;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.GetChatDTO;
import de.heedlesssoap.pinseekerbackend.entities.DirectMessage;
import de.heedlesssoap.pinseekerbackend.entities.enums.ChatState;
import de.heedlesssoap.pinseekerbackend.exceptions.ChatAlreadyExistsException;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidJWTTokenException;
import de.heedlesssoap.pinseekerbackend.repositories.ChatRepository;
import de.heedlesssoap.pinseekerbackend.repositories.DirectMessageRepository;
import de.heedlesssoap.pinseekerbackend.repositories.UserRepository;
import de.heedlesssoap.pinseekerbackend.utils.Constants;
import de.heedlesssoap.pinseekerbackend.utils.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatsService {
    final DirectMessageRepository directMessageRepository;
    final ChatRepository chatRepository;
    final UserRepository userRepository;
    final TokenService tokenService;

    public ChatsService(DirectMessageRepository directMessageRepository, ChatRepository chatRepository, UserRepository userRepository, TokenService tokenService) {
        this.directMessageRepository = directMessageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public GetChatDTO convertToChatDTO(Chat chat, ApplicationUser requestSender) {
        if(chat.getParticipants().size() != 2){
            System.out.println("Expected two Chat Participants but found " + chat.getParticipants().size() + "; chat_id: " + chat.getChatId());
            return null;
        }

        ApplicationUser otherUser = chat.getParticipants().stream()
                .filter(user -> !user.getUserId().equals(requestSender.getUserId()))
                .findFirst().get();
        ApplicationUserDTO otherUserDTO = new ApplicationUserDTO().fromApplicationUser(otherUser);

        Set<DirectMessageDTO> messageDTOs = chat.getMessages().stream()
                .map(message -> {
                    DirectMessageDTO directMessageDTO = new DirectMessageDTO();
                    directMessageDTO.setDirectMessageId(message.getDirectMessageId());
                    directMessageDTO.setSender(new ApplicationUserDTO().fromApplicationUser(message.getSender()));
                    directMessageDTO.setCreatedAt(message.getCreatedAt());

                    if (message.getSender().equals(requestSender)) {
                        directMessageDTO.setEncryptedMessage(message.getSenderEncryptedMessage());
                        directMessageDTO.setEncryptedAESKey(message.getSenderEncryptedAESKey());
                    }else{
                        directMessageDTO.setEncryptedMessage(message.getReceiverEncryptedMessage());
                        directMessageDTO.setEncryptedAESKey(message.getReceiverEncryptedAESKey());
                    }
                    return directMessageDTO;
                }).collect(Collectors.toSet());

        return new GetChatDTO(chat.getChatId(), otherUserDTO, messageDTOs, chat.getChatState());
    }

    public HashMap<Integer, HashMap<Integer, String>> convertToChatsList(List<Chat> chats, ApplicationUser requestSender) {
        HashMap<Integer, HashMap<Integer, String>> chat_list = new HashMap<>();
        chats.forEach(chat -> {
            if(chat.getParticipants().size() != 2){
                System.out.println("Expected two Chat Participants but found " + chat.getParticipants().size() + "; chat_id: " + chat.getChatId());
                return;
            }

            ApplicationUser otherUser = chat.getParticipants().stream()
                    .filter(user -> !user.getUserId().equals(requestSender.getUserId()))
                    .findFirst().get();

            DirectMessage last_message = directMessageRepository.getLastMessageInChat(chat)
                    .orElse(new DirectMessage());

            HashMap<Integer, String> chat_info = new HashMap<>();
            chat_info.put(0, otherUser.getUsername());
            chat_info.put(1, DateUtils.formatDate(last_message.getCreatedAt()));
            chat_info.put(2, last_message.getSender().equals(requestSender) ? last_message.getSenderEncryptedAESKey() : last_message.getReceiverEncryptedAESKey());
            chat_info.put(3, last_message.getSender().equals(requestSender) ? last_message.getSenderEncryptedMessage() : last_message.getReceiverEncryptedMessage());
            chat_list.put(chat.getChatId(), chat_info);
        });
        return chat_list;
    }

    public Chat getCheckedChat(Integer chat_id, ApplicationUser requesting_user) throws IllegalArgumentException, AccessDeniedException {
        Chat requested_chat = chatRepository.findById(chat_id)
                .orElseThrow(() -> new IllegalArgumentException(Constants.CHAT_NOT_FOUND));

        if(!requested_chat.getParticipants().contains(requesting_user)) {
            throw new AccessDeniedException(Constants.NOT_A_MEMBER);
        }

        return requested_chat;
    }

    private DirectMessage getCheckedMessage(Chat checked_chat, ApplicationUser sender, Integer directMessageId) throws IllegalArgumentException, AccessDeniedException {
        DirectMessage requested_message = directMessageRepository.findById(directMessageId)
                .orElseThrow(() -> new IllegalArgumentException(Constants.MESSAGE_NOT_FOUND));

        if(!checked_chat.getMessages().contains(requested_message)) {
            throw new IllegalArgumentException(Constants.ACCESS_DENIED);
        }

        if(!requested_message.getSender().equals(sender)) {
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        }

        return requested_message;
    }


    public HashMap<Integer, HashMap<Integer, String>> getChats(String token) throws InvalidJWTTokenException, IllegalArgumentException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);

        List<Chat> user_chats = chatRepository.findChatsByParticipantsContaining(Set.of(sender))
                .orElseThrow(() -> new IllegalArgumentException(Constants.USER_HAS_NO_CHATS));

        return this.convertToChatsList(user_chats, sender);
    }

    public GetChatDTO getChat(String token, Integer chatId) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Chat requested_chat = this.getCheckedChat(chatId, sender);

        return this.convertToChatDTO(requested_chat, sender);
    }

    public ResponseEntity<String> sendMessage(String token, Integer chatId, DirectMessage message) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Chat chat = getCheckedChat(chatId, sender);

        if(message.getSenderEncryptedMessage().isBlank() ||
            message.getSenderEncryptedAESKey().isBlank() ||
            message.getReceiverEncryptedMessage().isBlank() ||
            message.getReceiverEncryptedAESKey().isBlank()) {
            throw new IllegalArgumentException(Constants.MESSAGE_BLANK);
        }

        message.setSender(sender);
        message.setDirectMessageId(null);
        message.setCreatedAt(new Date());
        message.setChat(chat);
        DirectMessage sent = directMessageRepository.save(message);

        Set<DirectMessage> chat_messages = chat.getMessages();
        chat_messages.add(sent);
        chat.setMessages(chat_messages);

        chatRepository.save(chat);
        return new ResponseEntity<>(Constants.ACTION_SUCCESSFUL, HttpStatus.OK);
    }

    public ResponseEntity<String> openChat(String token, Integer targetUserId) throws InvalidJWTTokenException, UsernameNotFoundException, ChatAlreadyExistsException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        ApplicationUser target_user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UsernameNotFoundException(Constants.USERNAME_NOT_FOUND));

        if(chatRepository.doesChatWithUserAlreadyExist(sender, target_user)) {
            throw new ChatAlreadyExistsException();
        }

        Chat temp_chat = new Chat();
        temp_chat.setParticipants(Set.of(sender, target_user));
        chatRepository.save(temp_chat);

        return new ResponseEntity<>(Constants.ACTION_SUCCESSFUL, HttpStatus.OK);
    }

    public ResponseEntity<String> updateChat(String token, Integer chatId, ChatState state) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Chat chat = this.getCheckedChat(chatId, sender);
        chat.setChatState(state);

        chatRepository.save(chat);
        return new ResponseEntity<>(Constants.ACTION_SUCCESSFUL, HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<String> editMessage(String token, Integer chatId, Integer directMessageId, DirectMessage message) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Chat chat = this.getCheckedChat(chatId, sender);
        DirectMessage editable_message = this.getCheckedMessage(chat, sender, directMessageId);

        editable_message.setSenderEncryptedAESKey(message.getSenderEncryptedAESKey());
        editable_message.setSenderEncryptedMessage(message.getSenderEncryptedMessage());
        editable_message.setReceiverEncryptedAESKey(message.getReceiverEncryptedAESKey());
        editable_message.setReceiverEncryptedMessage(message.getReceiverEncryptedMessage());

        directMessageRepository.save(editable_message);
        return new ResponseEntity<>(Constants.ACTION_SUCCESSFUL, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteMessage(String token, Integer chatId, Integer directMessageId) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Chat chat = this.getCheckedChat(chatId, sender);
        DirectMessage message = this.getCheckedMessage(chat, sender, directMessageId);

        chat.getMessages().remove(message);
        chatRepository.save(chat);

        directMessageRepository.delete(message);
        return new ResponseEntity<>(Constants.ACTION_SUCCESSFUL, HttpStatus.OK);
    }
}