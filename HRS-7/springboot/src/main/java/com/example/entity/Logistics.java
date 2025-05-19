package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
* 物流类
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Logistics {
    private Integer id;
    private String order;
    private String userName;
    private String phone;
    private String inAddress;
    private String outAddress;
    private String applicationTime;//申请时间
    private String issuedTime;//发货时间
    private String outName;//发货人
    private Integer doctorId;
    private Integer hospitalId;
    private Integer status;
}
