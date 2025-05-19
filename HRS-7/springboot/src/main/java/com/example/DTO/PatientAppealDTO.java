package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientAppealDTO {
    //private Integer id;
    private String appealOrder;
    private String patientName;
    private String patientPhone;
    private String patientGender;
    private String hospital;
    private String department;
    private String doctor;
    private String file;
    private String appealContent;
    private String appealTime;
    private Integer status;
    private String filePath;
}
