package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Consultation {
    private Integer id;
    private Integer doctorId;
    private Integer userId;
    private Integer registerId;
    private String consultationDate;
    private String content;
}
