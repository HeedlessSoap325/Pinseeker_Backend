package de.heedlesssoap.pinseekerbackend.controllers;

import de.heedlesssoap.pinseekerbackend.entities.DTOs.GetChatDTO;
import de.heedlesssoap.pinseekerbackend.entities.DirectMessage;
import de.heedlesssoap.pinseekerbackend.exceptions.ChatAlreadyExistsException;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidJWTTokenException;
import de.heedlesssoap.pinseekerbackend.services.ChatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chats")
@CrossOrigin("*")
public class ChatsController {
    private final ChatsService chatsService;

    public ChatsController(ChatsService chatsService) {
        this.chatsService = chatsService;
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, String>> openChat(@RequestHeader("Authorization") String token, @RequestHeader("target_user_id") Integer target_user_id) throws InvalidJWTTokenException, UsernameNotFoundException, ChatAlreadyExistsException {
        return chatsService.openChat(token, target_user_id);
    }

    @PutMapping("/")
    public ResponseEntity<Map<String, String>> updateChat(@RequestHeader("Authorization") String token, @RequestHeader("chat_id") Integer chat_id) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        return chatsService.toggleChatState(token, chat_id);
    }

    @GetMapping("/")
    public Map<Integer, Map<String, String>> getChats(@RequestHeader("Authorization") String token) throws InvalidJWTTokenException, IllegalArgumentException {
        return chatsService.getChats(token);
    }

    @GetMapping("/{chat_id}")
    public ResponseEntity<GetChatDTO> getChat(@RequestHeader("Authorization") String token, @PathVariable("chat_id") Integer chat_id) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        return chatsService.getChat(token, chat_id);
    }

    @PostMapping("/{chat_id}")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestHeader("Authorization") String token, @PathVariable("chat_id") Integer chat_id, @RequestBody DirectMessage message) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        return chatsService.sendMessage(token, chat_id, message);
    }

    @PutMapping("/{chat_id}")
    public ResponseEntity<Map<String, String>> editMessage(@RequestHeader("Authorization") String token, @PathVariable("chat_id") Integer chat_id, @RequestHeader("message_id") Integer direct_message_id, @RequestBody DirectMessage message) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        return chatsService.editMessage(token, chat_id, direct_message_id, message);
    }

    @DeleteMapping("/{chat_id}")
    public ResponseEntity<Map<String, String>> deleteMessage(@RequestHeader("Authorization") String token, @PathVariable("chat_id") Integer chat_id, @RequestHeader("message_id") Integer direct_message_id) throws InvalidJWTTokenException, IllegalArgumentException, AccessDeniedException {
        return chatsService.deleteMessage(token, chat_id, direct_message_id);
    }
}