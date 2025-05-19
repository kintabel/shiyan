package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiskFiles {
    private Integer id;
    private String name;
    private Integer userId;
    private String file;
    private String type;
    private String createTime;
    private Double size;
    private String diskName;
}
