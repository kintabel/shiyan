package com.example.mapper;

import com.example.entity.Department;
import com.example.entity.Hospital;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface HospitalMapper {
    @Select("select * from `cq-hospital`.hospital")
    List<Hospital> selectAllData(Hospital hospital);

    @Select("select * from `cq-hospital`.hospital where id = #{id}")
    Hospital  selectById(int id);

    @Select("select * from `cq-hospital`.department where hid = #{id}")
    List<Department> selectAllDepartments(int id);


    @Select("select hospital_id from  `cq-hospital`.admin where id = #{id}")
    Integer getHospitalById(Integer id);

    @Select("select * from `cq-hospital`.department where id = #{departmentId}")
    Hospital selectDepById(Integer departmentId);

    @Select("select name from `cq-hospital`.hospital where id = #{hospitalId}")
    String getHospitalName(Integer hospitalId);
}
