package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 请假申请表对应的实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveApplication {
    private int id;          // 请假申请id
    private int doctorId;         // 医生id
    private int departmentId;     // 科室id
    private int hospitalId;       // 医院id
    private String leaveType;     // 请假类型
    private LocalDate leaveDate;     // 请假日期
    private String applicationDate; // 请假提交日期
    private String reason;        // 请假原因
    private int approvalStatus;   // 请假审批状态

}