package com.example.mapper;

import com.example.DTO.PatientAppealDTO;
import com.example.DTO.SubPrescripetionDTO;
import com.example.entity.Appeal;
import com.example.entity.Register;
import com.example.entity.RegistrationDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface RegisterMapper {
    @Insert("insert into `cq-hospital`.register (`order`, hospital_id, department_id, doctor_id, user_id, order_time, register_time, price,status, shift_type) VALUES (#{order}, #{hospitalId}, #{departmentId}, #{doctorId}, #{userId}, #{orderTime}, #{registerTime}, #{price},#{status},#{shiftType})")
    void insert(Register register);

    @Select("select * from `cq-hospital`.register where register.order = #{order}")
    Register selectOrder(String order);

    RegistrationDetail selectOrderDetail(Long order);

    @Select("select user_id AS patient_id , hospital_id, department_id, doctor_id from `cq-hospital`.register where register.order = #{order}")
    SubPrescripetionDTO selectByOrder(String order);

    @Insert("insert into `cq-hospital`.appeal (appeal_order, register_order, content, file_id, date, status) VALUES (#{appealOrder}, #{registerOrder}, #{content}, #{fileId}, #{date}, #{status})")
    void insertAppeal(Appeal appeal);

    List<PatientAppealDTO> patientAppealPage(Integer userId);

    PatientAppealDTO selectByAppealOrder(String appealOrder);

    void handleAppeal(String appealOrder);
}
