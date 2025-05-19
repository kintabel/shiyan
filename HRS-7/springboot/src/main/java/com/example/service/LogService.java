package com.example.service;

import com.example.entity.OperationLog;
import com.example.mapper.OperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    @Autowired
    private OperationLogMapper logMapper;
    @Async
    public void saveAsync(OperationLog log) {
        logMapper.insert(log);
    }
}