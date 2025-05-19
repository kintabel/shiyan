package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubRegistrationDTO {
    private Integer doctorId;
    private Integer userId;
    private String registrationDate;
    private String registrationTimeSlot;
    private Integer registrationPrice;
    private String shiftType;
    private Integer status;
}
