package com.example.service;
import com.example.entity.Message;
import com.example.entity.User;
import com.example.mapper.DoctorMapper;
import com.example.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class MessageService {

    @Resource
    private  MessageMapper messageMapper;
    @Resource
    private DoctorMapper doctorMapper;

    public MessageService(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    public void saveMessage(Message message) {
        if (message.getSenderType().equals("patient")) {
            message.setReceiverId(doctorMapper.selectDoctorById(String.valueOf(message.getReceiverId())).getUserId());
        }
        message.setTimestamp(String.valueOf(new Date()));
        messageMapper.insertMessage(message);
    }

    public List<Message> getConversation(Long user1Id, Long user2Id,String role) {
        if (role.equals("patient")) {
            user2Id = Long.valueOf(doctorMapper.selectDoctorById(String.valueOf(user2Id)).getUserId());
        }
        return messageMapper.selectConversation(user1Id, user2Id);
    }

    public List<User> selectPatients(Integer  doctorId) {
        return messageMapper.selectPatients(doctorId);
    }
}