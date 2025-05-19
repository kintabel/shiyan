package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* 添加住院信息，疾病和手术
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddInformationDTO {
    private Integer hospitalizationId;
    private String disease;
    private String surgery;
}
