package com.example.mapper;

import com.example.DTO.SubPrescripetionDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
@Mapper
public interface PrescribeMedicationsMapper {
    @Insert("insert into `cq-hospital`.prescribe_medications (`order`, patient_id, hospital_id, department_id, doctor_id, date) VALUES (#{order}, #{patientId}, #{hospitalId}, #{departmentId}, #{doctorId}, #{date})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertPrescriptions(SubPrescripetionDTO subPrescripetionDTO);

    @Insert("insert into `cq-hospital`.medication_mid(pm_id, medication, number, content) VALUES ( #{id},#{medication}, #{number}, #{content})")
    void insertPrescriptionsMid(String medication, Integer number, String content, Integer id);
}
