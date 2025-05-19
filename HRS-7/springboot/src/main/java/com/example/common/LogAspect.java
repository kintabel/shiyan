package com.example.common;

import com.example.entity.Admin;
import com.example.entity.OperationLog;
import com.example.mapper.OperationLogMapper;
import com.example.service.LogService;
import com.example.utils.TokenUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Component
@Slf4j
public class LogAspect {
    
    @Autowired
    private OperationLogMapper logMapper;
    
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LogService logService;
    
    // 定义切点：标注了@Log注解的方法
    @Pointcut("@annotation(com.example.common.Log)")
    public void logPointCut() {}
    
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取操作日志注解
        Log logAnnotation = method.getAnnotation(Log.class);
        
        // 记录开始时间
        long beginTime = System.currentTimeMillis();
        
        try {
            // 执行原方法
            Object result = joinPoint.proceed();
            
            // 计算方法执行耗时
            long time = System.currentTimeMillis() - beginTime;
            
            // 保存操作日志
            saveLog(joinPoint, logAnnotation, time, null);
            
            return result;
        } catch (Exception e) {
            long time = System.currentTimeMillis() - beginTime;
            saveLog(joinPoint, logAnnotation, time, e);
            throw e;
        }
    }
    
    private void saveLog(ProceedingJoinPoint joinPoint, Log logAnnotation, long time, Exception e) throws JsonProcessingException {

        Admin currentUser = (Admin) TokenUtils.getCurrentUser();
        
        OperationLog operationLog = new OperationLog();
        operationLog.setModule(logAnnotation.module());
        operationLog.setType(logAnnotation.type());
        operationLog.setDescription(logAnnotation.description());
        
        if (currentUser != null) {
            operationLog.setUserId(currentUser.getId());
            operationLog.setUserName(currentUser.getUsername());
        }
        
        // 设置请求信息
        operationLog.setMethod(joinPoint.getSignature().getName());
        operationLog.setParams(objectMapper.writeValueAsString(joinPoint.getArgs()));
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = localDateTime.format(formatter);
        operationLog.setCreateTime(formattedDateTime);
        
        // 如果有异常，记录异常信息
        if (e != null) {
            operationLog.setDescription(operationLog.getDescription() + " [异常: " + e.getMessage() + "]");
        }
        
        // 异步保存日志
        logService.saveAsync(operationLog);
    }
}