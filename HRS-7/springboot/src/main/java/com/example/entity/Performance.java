package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Performance {
    private int hospitalId;
    private int departmentId;
    private int doctorId;
    private float performance;
    private float salary;
}
