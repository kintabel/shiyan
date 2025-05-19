package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDTO {
    private Integer id;
    private String name;
    private Integer stock;
    private String department;
    private String manufacturer;
    private String sellBy;
    private Integer price;
    private Integer status;
}
