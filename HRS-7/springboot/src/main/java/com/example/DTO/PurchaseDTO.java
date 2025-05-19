package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* 进货DTO类
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDTO {
    private String name;
    private Integer quantity;
    private String supplierAddress;
    private String supplierLeader;
    private String supplierName;
    private String supplierPhone;
    private Integer supplierPrice;
    private String date;
    private Integer totalPrice;
    private Integer hospitalAdminId;
    private String department;
}
