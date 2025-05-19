package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* 健康科普
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthArticle {
    private Integer id;                 // 文章ID
    private String title;              // 文章标题
    private String content;            // 文章内容
    private String publishTime;        // 发布时间
    private Integer viewCount;         // 阅读量
    private Integer likeCount;         // 点赞数
    private Integer status;            // 状态(0-已发布,1-已下架)
    private String category;           // 分类(如:饮食健康,运动保健等)
    private Integer hospitalId;        // 所属医院ID
}