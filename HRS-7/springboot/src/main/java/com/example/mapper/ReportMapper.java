package com.example.mapper;

import com.example.DTO.AdmissionDTO;
import com.example.DTO.InformationDTO;
import com.example.DTO.ReportDTO;
import com.example.entity.Report;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReportMapper {
    @Insert("insert into `cq-hospital`.report (report_type, patient_gender, patient_age, patient_height, patient_weight, blood_pressure, heart_rate, complete_blood_count, urinalysis, contagious_disease) " +
            "VALUES (#{reportType}, #{patientGender}, #{patientAge}, #{patientHeight}, #{patientWeight}, #{bloodPressure}, #{heartRate}, #{completeBloodCount}, #{urinalysis}, #{contagiousDisease})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Integer insert(ReportDTO reportDTO);

    @Insert("insert into `cq-hospital`.information (in_report_id,admission_time) VALUES (#{inReportId},#{admissionTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Integer insertInformation(InformationDTO informationDTO);

    void insertHospitalization(String order, Integer informationId);

    @Update("update `cq-hospital`.report set information_id = #{informationId} where id = #{id}")
    void updateReport(Integer id,Integer informationId);

    List<Report> selectReportByDoctorId(Integer doctorUserId,String reportType);

    List<Report> selectReportsByConditions(@Param("doctorUserId") Integer doctorUserId, @Param("reportType") String reportType, @Param("userName") String userName, @Param("startDate") String startDate, @Param("endDate") String endDate);

    void admisson(AdmissionDTO admissionDTO);

    @Update("update `cq-hospital`.register set status = 3 where `order` = #{order}")
    void updateStatus(String order);
}
