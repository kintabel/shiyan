package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* 报告单
* */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    private Integer id;
    private String userName;
    private String doctorName;
    private String hospitalName;
    private String departmentName;
    private String reportType;
    private Integer patientAge;
    private String patientHeight;
    private String patientWeight;
    private String patientGender;
    private String bloodPressure;
    private String heartRate;
    private String completeBloodCount;
    private String urinalysis;
    private String contagiousDisease;
    private Integer informationId;
}
