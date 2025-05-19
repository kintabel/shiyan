package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private Integer id;
    private String content;
    private String senderType; // "patient" or "doctor"
    private Integer senderId;
    private Integer receiverId;
    private String timestamp;

}