package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* 资源统计DTO
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDTO {
    private String name; //名称
    private Integer stock; //数量
    private String time; //具体保质时间
    private Integer status; //状态;
}
