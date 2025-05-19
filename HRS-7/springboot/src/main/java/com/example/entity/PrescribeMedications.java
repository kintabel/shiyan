package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/*
* 开药的DTO
* */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescribeMedications {
    //患者id , 医院id, 部门id , 医生id , 药品id , 数量 , 处方内容
    private Integer id;
    private Integer patientId;
    private Integer hospitalId;
    private Integer departmentId;
    private Integer doctorId;
    private String medication;
    private String number;
    private String content;
    private String date;
}
