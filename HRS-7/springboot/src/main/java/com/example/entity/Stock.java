package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* 库存
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    private Integer id;
    private Integer hospitalId;
    private Integer departmentId;
    private String name;
    //库存
    private Integer stock;
    //生产日期
    private String manufacturer;
    //有效期
    private String sellBy;
    private Integer price;
    //库存类型  1.药物  2.仪器
    private Integer type;
    //状态 0.停用 1.正常  2.过期  3.缺货
    private Integer status;
}
