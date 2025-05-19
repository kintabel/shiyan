package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdmissionDTO {
    private Integer id;
    private String userName;
    private String doctorName;
    private String hospitalName;
    private String departmentName;
    private String admissionDate;
    private String admissionType;
    private Integer prepayment;
    private String surgery;
}
