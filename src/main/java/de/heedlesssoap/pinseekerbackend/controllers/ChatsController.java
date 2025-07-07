package de.heedlesssoap.pinseekerbackend.controllers;

import de.heedlesssoap.pinseekerbackend.entities.DTOs.GetChatDTO;
import de.heedlesssoap.pinseekerbackend.entities.DirectMessage;
import de.heedlesssoap.pinseekerbackend.entities.enums.ChatState;
import de.heedlesssoap.pinseekerbackend.exceptions.ChatAlreadyExistsException;
import de.heedlesssoap.pinseekerbackend.exceptions.InvalidJWTTokenException;
import de.heedlesssoap.pinseekerbackend.services.ChatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/chats")
@CrossOrigin("*")
public class ChatsController {

    private final ChatsService chatsService;

    public ChatsController(ChatsService chatsService) {
        this.chatsService = chatsService;
    }

    @ExceptionHandler(InvalidJWTTokenException.class)
    public ResponseEntity<String> handleInvalideJWTTokenException(InvalidJWTTokenException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ChatAlreadyExistsException.class)
    public ResponseEntity<String> handleChatAlreadyExistsException(ChatAlreadyExistsException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @PostMapping("/")
    public ResponseEntity<String> openChat(@RequestHeader("Authorization") String token, @RequestHeader("target_user_id") Integer target_user_id) throws InvalidJWTTokenException, UsernameNotFoundException, ChatAlreadyExistsException {
        return chatsService.openChat(token, target_user_id);
    }

    @PutMapping("/")
    public ResponseEntity<String> updateChat(@RequestHeader("Authorization") String token, @RequestHeader("chat_id") Integer chat_id, @RequestHeader("state") ChatState state) throws InvalidJWTTokenException, IllegalArgumentException {
        return chatsService.updateChat(token, chat_id, state);
    }

    @GetMapping("/")
    public HashMap<Integer, HashMap<Integer, String>> getChats(@RequestHeader("Authorization") String token) throws InvalidJWTTokenException, IllegalArgumentException {
        return chatsService.getChats(token);
    }

    @GetMapping("/{chat_id}")
    public GetChatDTO getChat(@RequestHeader("Authorization") String token, @PathVariable("chat_id") Integer chat_id) throws InvalidJWTTokenException, IllegalArgumentException {
        return chatsService.getChat(token, chat_id);
    }

    @PostMapping("/{chat_id}")
    public ResponseEntity<String> sendMessage(@RequestHeader("Authorization") String token, @PathVariable("chat_id") Integer chat_id, @RequestBody DirectMessage message) throws InvalidJWTTokenException, IllegalArgumentException {
        return chatsService.sendMessage(token, chat_id, message);
    }

    @PutMapping("/{chat_id}")
    public ResponseEntity<String> editMessage(@RequestHeader("Authorization") String token, @PathVariable("chat_id") Integer chat_id, @RequestHeader("message_id") Integer direct_message_id, @RequestBody DirectMessage message) throws InvalidJWTTokenException, IllegalArgumentException {
        return chatsService.editMessage(token, chat_id, direct_message_id, message);
    }

    @DeleteMapping("/{chat_id}")
    public ResponseEntity<String> deleteMessage(@RequestHeader("Authorization") String token, @PathVariable("chat_id") Integer chat_id, @RequestHeader("message_id") Integer direct_message_id) throws InvalidJWTTokenException, IllegalArgumentException {
        return chatsService.deleteMessage(token, chat_id, direct_message_id);
    }
}