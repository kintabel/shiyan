package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
    private Integer id;
    private String orderId;
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
