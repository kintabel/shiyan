package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescribeDetailDTO {
    private String order;
    private String hospitalName;
    private String departmentName;
    private String doctorName;
    private String date;
    private int price;
    private Integer pmId;
    private String userName;
    private String userPhone;
    private String userGender;
    private Integer status;
    private String medicines;
    private Integer number;
    private String content;
}
