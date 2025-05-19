package com.example.common.config;

import com.example.DTO.WaitingQueuesDTO;
import com.example.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

public class WaitingQueueHandler extends TextWebSocketHandler {

    @Autowired
    private DoctorService doctorService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // 假设 doctorId 为 1
        Integer doctorId = 1;
        String registerTime = "2024-01-01";
        List<WaitingQueuesDTO> waitingQueues = doctorService.selectWaitingQueues(doctorId, registerTime);
        session.sendMessage(new TextMessage(waitingQueues.toString()));
    }
}