package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hospitalization {
    private Integer id;
    private String userName;
    private String doctorName;
    private String hospitalName;
    private String departmentName;
}
