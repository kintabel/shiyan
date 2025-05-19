package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescripetionDataDTO {
    private List<PrescriptionDTO> prescriptions;
    private String order;
}
