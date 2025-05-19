package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    private Integer id;
    //名字
    private String name;
    //部门id
    private Integer departmentId;
    //手机号
    private String phone;
    //性别
    private String gender;
    //所属医院
    private Integer hospitalId;
    //职称
    private Integer status;
    //对应用户id
    private Integer userId;
    //简介
    private String introduction;
    private String appealedNumber;
    private double salary;
    private String finalSalary;
    private Integer evaluationCount;
    private double evaluation;
}
