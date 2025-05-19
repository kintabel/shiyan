package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
* 操作日志类
* */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {
    private Integer id;
    private String module;       // 操作模块
    private String type;         // 操作类型
    private String description;  // 操作描述
    private Integer userId;         // 操作人ID
    private String userName;     // 操作人账户
    private String method;       // 请求方法
    private String params;       // 请求参数
    private String createTime;     // 操作时间
}