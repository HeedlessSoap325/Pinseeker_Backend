package de.heedlesssoap.pinseekerbackend.services;

import de.heedlesssoap.pinseekerbackend.entities.ApplicationUser;
import de.heedlesssoap.pinseekerbackend.entities.Chat;
import de.heedlesssoap.pinseekerbackend.entities.DTOs.GetChatDTO;
import de.heedlesssoap.pinseekerbackend.entities.DirectMessage;
import de.heedlesssoap.pinseekerbackend.entities.enums.ChatState;
import de.heedlesssoap.pinseekerbackend.exceptions.ChatAlreadyExistsException;
import de.heedlesssoap.pinseekerbackend.exceptions.ChatNotWritableException;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidJWTTokenException;
import de.heedlesssoap.pinseekerbackend.repositories.ChatRepository;
import de.heedlesssoap.pinseekerbackend.repositories.DirectMessageRepository;
import de.heedlesssoap.pinseekerbackend.repositories.UserRepository;
import de.heedlesssoap.pinseekerbackend.utils.Constants;
import de.heedlesssoap.pinseekerbackend.utils.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    private ApplicationUser getOtherUserInChat(Chat chat, ApplicationUser sender) throws IllegalStateException {
        if(chat.getParticipants().size() != 2){
            System.out.println("Expected two Chat Participants but found " + chat.getParticipants().size() + "; chat_id: " + chat.getChatId());
            throw new IllegalArgumentException(Constants.CHAT_INVALID_SIZE);
        }

        return chat.getParticipants().stream()
                .filter(user -> !user.getUserId().equals(sender.getUserId()))
                //NOTE: This is fine, because we checked earlier if the number of participants is 2
                //NOTE: and because getParticipants returns a Set, we can be sure, that there
                //NOTE: has to be another User, that is not the sender.
                .findFirst().get();
    }

    private Chat getCheckedChat(Integer chat_id, ApplicationUser requesting_user) throws IllegalArgumentException, AccessDeniedException {
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

    private boolean isMessageNotValid(DirectMessage message){
        return message.getSenderEncryptedMessage().isBlank() ||
                message.getSenderEncryptedAesKey().isBlank() ||
                message.getReceiverEncryptedMessage().isBlank() ||
                message.getReceiverEncryptedAesKey().isBlank();
    }

    public Map<Integer, Map<String, String>> getChats(String token) throws InvalidJWTTokenException, IllegalArgumentException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        List<Chat> user_chats = chatRepository.findChatsByParticipants(sender)
                .orElse(new ArrayList<>());

        Map<Integer, Map<String, String>> chat_list = new HashMap<>();
        user_chats.forEach(chat -> {
            ApplicationUser otherUser = getOtherUserInChat(chat, sender);

            DirectMessage last_message = directMessageRepository.getLastMessageInChat(chat)
                    .orElse(new DirectMessage());

            Map<String, String> chat_info = new HashMap<>();
            chat_info.put("username", otherUser.getUsername());
            chat_info.put("created_at", DateUtils.formatDate(last_message.getCreatedAt()));
            chat_info.put("last_message_encrypted_aes_key", last_message.getSender().equals(sender) ? last_message.getSenderEncryptedAesKey() : last_message.getReceiverEncryptedAesKey());
            chat_info.put("last_message_encrypted", last_message.getSender().equals(sender) ? last_message.getSenderEncryptedMessage() : last_message.getReceiverEncryptedMessage());
            chat_list.put(chat.getChatId(), chat_info);
        });
        return chat_list;
    }

    public ResponseEntity<GetChatDTO> getChat(String token, Integer chatId) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Chat requested_chat = this.getCheckedChat(chatId, sender);

        return new ResponseEntity<>(new GetChatDTO().fromChat(requested_chat, getOtherUserInChat(requested_chat, sender)), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> sendMessage(String token, Integer chatId, DirectMessage message) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Chat chat = getCheckedChat(chatId, sender);

        if(chat.getChatState() == ChatState.READ_ONLY){
            throw new ChatNotWritableException();
        }
        if(isMessageNotValid(message)) {
            throw new IllegalArgumentException(Constants.MESSAGE_BLANK);
        }

        message.setSender(sender);
        message.setDirectMessageId(null);
        message.setCreatedAt(new Date());
        message.setChat(chat);
        DirectMessage sent = directMessageRepository.save(message);

        chat.getMessages().add(sent);
        chat.setChatState(ChatState.ACTIVE);
        chatRepository.save(chat);
        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> openChat(String token, Integer targetUserId) throws InvalidJWTTokenException, UsernameNotFoundException, ChatAlreadyExistsException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        if(sender.getUserId().equals(targetUserId)) {
            throw new IllegalArgumentException(Constants.NOT_ALLOWED);
        }
        ApplicationUser target_user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UsernameNotFoundException(Constants.USERNAME_NOT_FOUND));

        if(chatRepository.doesChatWithUserAlreadyExist(sender, target_user)) {
            throw new ChatAlreadyExistsException();
        }

        Chat temp_chat = new Chat();
        temp_chat.setParticipants(Set.of(sender, target_user));
        chatRepository.save(temp_chat);

        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> toggleChatState(String token, Integer chatId) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Chat chat = this.getCheckedChat(chatId, sender);
        if(chat.getChatState() == ChatState.READ_ONLY){
            throw new ChatNotWritableException();
        }
        chat.setChatState(chat.getChatState() == ChatState.ACTIVE ? ChatState.INACTIVE : ChatState.ACTIVE);

        chatRepository.save(chat);
        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> editMessage(String token, Integer chatId, Integer directMessageId, DirectMessage new_message) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Chat chat = this.getCheckedChat(chatId, sender);
        if(isMessageNotValid(new_message)) {
            throw new IllegalArgumentException(Constants.MESSAGE_BLANK);
        }
        if(chat.getChatState() == ChatState.READ_ONLY){
            throw new ChatNotWritableException();
        }

        DirectMessage previous_message = this.getCheckedMessage(chat, sender, directMessageId);

        new_message.setDirectMessageId(previous_message.getDirectMessageId());
        new_message.setSender(previous_message.getSender());
        new_message.setChat(previous_message.getChat());
        new_message.setCreatedAt(previous_message.getCreatedAt());

        directMessageRepository.save(new_message);
        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> deleteMessage(String token, Integer chatId, Integer directMessageId) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        ApplicationUser sender = tokenService.getSenderFromJWT(token);
        Chat chat = this.getCheckedChat(chatId, sender);
        if(chat.getChatState() == ChatState.READ_ONLY){
            throw new ChatNotWritableException();
        }
        DirectMessage message = this.getCheckedMessage(chat, sender, directMessageId);

        chat.getMessages().remove(message);
        chatRepository.save(chat);

        directMessageRepository.delete(message);
        return new ResponseEntity<>(Map.of("message", Constants.ACTION_SUCCESSFUL), HttpStatus.OK);
    }
}