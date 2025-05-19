package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationDTO {
    private String registerOrder;
    private Integer evaluationType;
    private String evaluationContent;
    private String evaluationDate;
}
