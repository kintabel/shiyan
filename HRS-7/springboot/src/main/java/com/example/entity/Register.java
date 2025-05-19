package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* 挂号
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Register {
    private int id;
    private String order;
    private int hospitalId;
    private int departmentId;
    private int doctorId;
    private int userId;
    private String orderTime;
    private String registerTime;
    private int price;
    private String shiftType;
    private int status;
}
