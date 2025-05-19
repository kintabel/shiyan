package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubPrescripetionDTO {
    private Integer id;
    public Integer patientId;
    public Integer doctorId;
    public Integer hospitalId;
    public Integer departmentId;
    public String date;
    public String order;
}
