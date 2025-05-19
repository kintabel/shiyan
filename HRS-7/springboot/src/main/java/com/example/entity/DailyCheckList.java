package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyCheckList {
    private Integer id;
    private Integer hospitalizationIdOrinformationId;
    private String date;
    private String medicine;
    private String transfusion;
    private String surgery;
    private String surgeryTime;
    private String dailySituation;
    private Integer cost;
}
