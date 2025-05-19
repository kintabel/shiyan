package com.example.mapper;

import com.example.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperationLogMapper {
    int insert(OperationLog log);

    //List<OperationLog> selectByCondition(OperationLog condition);
    //int deleteByIds(Long[] ids);
}