package com.example.DTO;

import lombok.Data;

@Data
public class AutoScheduleRequest {
    private Integer departmentId;
    private Integer adminId;
    private String startDate;
}