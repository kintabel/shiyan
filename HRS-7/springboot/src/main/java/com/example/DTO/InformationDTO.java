package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InformationDTO {
    private Integer id;
    private Integer inReportId;
    private String admissionTime;
}
