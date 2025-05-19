package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private Integer id;
    private Integer hospitalId; // 从登录用户上下文获取
    private Integer departmentId;
    private Integer doctorId;
    private LocalDate scheduleDate;
    private String shiftType;
    private Integer fatigueScore;
    private LocalDateTime createTime;
}