package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 公告信息表
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notice implements Serializable {
    private Integer id;          // 动态ID
    private String title;       // 动态标题 (10字以内)
    private String content;     // 动态内容 (100字以内)
    private String publishTime; // 发布时间 (yyyy-MM-dd)
    private Integer status;     // 状态 (0-显示, 1-隐藏)
    private Integer hospitalId; // 医院ID
}