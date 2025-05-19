package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appeal {
    private Integer id;
    private String appealOrder;
    private String registerOrder;
    private String content;
    private Integer fileId;
    private String date;
    private Integer status;
}
