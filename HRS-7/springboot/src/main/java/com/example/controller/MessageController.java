package com.example.controller;
import com.example.common.Result;
import com.example.entity.Message;
import com.example.entity.User;
import com.example.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/chat")
@Slf4j
public class MessageController {

    @Resource
    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public Result handleChatMessage(@RequestBody Message message) {
        log.info("Received message: {}", message);
        messageService.saveMessage(message);
        return Result.success();
    }

    @GetMapping("/messages/{user1Id}/{user2Id}/{role}")
    @ResponseBody
    public Result getMessages(
            @PathVariable Long user1Id,
            @PathVariable Long user2Id,
            @PathVariable String role) {
        List<Message> messages = messageService.getConversation(user1Id, user2Id,role);
        return Result.success(messages);
    }

    @GetMapping("/selectPatients")
    @ResponseBody
    public Result selectPatients(@RequestParam Integer doctorId) {
        log.info("Received doctorId: {}", doctorId);
        List<User> patients = messageService.selectPatients(doctorId);
        return Result.success(patients);
    }
}