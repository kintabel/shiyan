package com.example.entity;
/*
* 医院实体类
* */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hospital {
    private Integer id;
    //名字
    private String name;
    //地址
    private String address;
    //电话
    private String phone;
    //医院等级
    private Integer level;
    //部门
    private Department[] departments;
    //简介
    private String introduction;
    //擅长疾病
    private String specialty;

}
