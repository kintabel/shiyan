package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDetail {
    private String order;
    private String hospitalName;
    private String departmentName;
    private String doctorName;
    private String orderTime;
    private String registerTime;
    private String shiftType;
    private int price;
    private String userName;
    private String userPhone;
    private String userGender;
    private Integer status;
}
