package com.example.common;

import java.lang.annotation.*;
/*
* 日志注解
* */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    String module() default "";      // 操作模块
    String type() default "";        // 操作类型(如:新增、删除、修改)
    String description() default ""; // 操作描述
}