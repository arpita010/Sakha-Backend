package com.sakha.controller;

import com.sakha.client.request.SakhaChatRequest;
import com.sakha.client.response.SakhaChatResponse;
import com.sakha.entity.ChatRequest;
import com.sakha.entity.ChatResponse;
import com.sakha.service.AuthService;
import com.sakha.service.ChatService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/sakha/v2/chat")
@CrossOrigin(origins = "*")
public class ChatController {
  @Autowired private ChatService chatService;
  @Autowired private AuthService authService;

  @PostMapping("/message")
  public ResponseEntity<ChatResponse> getMessage(@RequestBody ChatRequest chatRequest)
      throws Exception {
    if (!authService.validate(chatRequest.getAuthToken(), chatRequest.getUserId()))
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST,
          "Authentication FAILED (Either user is not logged in or AuthToken is incorrect)");
    SakhaChatRequest sakhaChatRequest = new SakhaChatRequest(chatRequest.getMessage());
    SakhaChatResponse sakhaChatResponse = chatService.getSakhaResponse(sakhaChatRequest);
    ChatResponse chatResponse = null;
    if (sakhaChatResponse != null) {
      chatResponse = new ChatResponse();
      chatResponse.setMessage(chatRequest.getMessage());
      chatResponse.setSakhaResponse(sakhaChatResponse.getSakhaResponse());
      chatService.saveChat(chatResponse, chatRequest.getUserId());
      return ResponseEntity.status(HttpStatus.OK).body(chatResponse);
    } else {
      throw new HttpClientErrorException(
          HttpStatus.BAD_REQUEST, "Some Error occured while messaging...");
    }
  }

  @GetMapping("/fetch/user/{userId}")
  public ResponseEntity<List<Chat>> findAllChatsByUserId(@PathVariable("userId") Long userId) {
    List<Chat> list = chatService.findAllByUserId(userId);
    if (list == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    return ResponseEntity.status(HttpStatus.OK).body(list);
  }
  
}
