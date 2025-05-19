package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescribeUserDTO {
    private String userName;
    private String userGender;
    private String userPhone;
    private String date;
    private String order;
}
