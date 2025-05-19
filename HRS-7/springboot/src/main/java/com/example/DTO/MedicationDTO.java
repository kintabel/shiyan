package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicationDTO {
    public Integer id;
    private Integer pmId;
    private Integer medicationId;
    private Integer number;
}
