package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveApplicationDTO {
    private int doctorId;         // 医生id
    private LocalDate scheduleDate;     // 请假日期
    private String shiftType;     // 班次类型
    private String leaveType;     // 请假类型
    private String reason;        // 请假原因
}
