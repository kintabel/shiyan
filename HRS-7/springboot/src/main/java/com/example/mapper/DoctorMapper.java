package com.example.mapper;

import com.example.DTO.AddInformationDTO;
import com.example.DTO.PrescribeDetailDTO;
import com.example.DTO.PrescribeUserDTO;
import com.example.DTO.WaitingQueuesDTO;
import com.example.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface DoctorMapper {


    public List<Doctor> selectDocByHidAndDepId(String hid, String depId);

    @Select("select * from `cq-hospital`.doctor where id = #{doctorId}")
    Doctor selectDoctorById(String doctorId);

    List<RegistrationDetail> selectAppointments(Integer id , String date);

    @Select("select * from `cq-hospital`.doctor where user_id = #{id}")
    Doctor selectDoctorByUserId(Integer id);

    List<Consultation> getConsultationRecords(String userName);

    @Insert("insert into `cq-hospital`.consultation (doctor_id, user_id, register_id, consultation_date, content) VALUES (#{doctorId}, #{userId}, #{registerId}, #{consultationDate}, #{content})")
    void addConsultationRecord(Consultation consultation);

    @Delete("delete from `cq-hospital`.consultation where id = #{recordId}")
    void deleteConsultationRecord(String recordId);

    @Update("update `cq-hospital`.register set status = 4 where `order` = #{registerId}")
    void cancelConsultation(Long registerId);

    @Update("update `cq-hospital`.register set status = 1 where `order` = #{registerId}")
    void completeConsultation(Long registerId);

    @Update("update `cq-hospital`.register set status = 2 where `order` = #{order}")
    void prescribeMedications(String order);

    @Update("update `cq-hospital`.register set status = 3 where `order` = #{registerId}")
    void submitOrder(Long registerId);

    List<PrescribeDetailDTO> selectPrescribe(Integer id,String order);

    List<PrescribeUserDTO> selectRegisterByDocId(Integer id);

    @Select("select * from `cq-hospital`.doctor where name = #{doctorName}")
    Doctor selectDoctorByName(String doctorName);

    @Select("SELECT COUNT(*) FROM `cq-hospital`.doctor WHERE department_id = #{departmentId}")
    int countDoctorsByDepartment(Integer departmentId);

    List<Hospitalization> selectHospitalizationById(Integer id);

    void informationAdd(AddInformationDTO information);

        @Insert("insert into `cq-hospital`.daily_checklist (information_id, date, medicine, transfusion, surgery, surgery_time, daily_situation, cost) VALUES (#{informationId}, #{date}, #{medicine}, #{transfusion}, #{surgery}, #{surgeryTime}, #{dailySituation}, #{cost})")
        void dailyAdd(@Param("informationId") Integer informationId,
                      @Param("date") String date,
                      @Param("medicine") String medicine,
                      @Param("transfusion") String transfusion,
                      @Param("surgery") String surgery,
                      @Param("surgeryTime") String surgeryTime,
                      @Param("dailySituation") String dailySituation,
                      @Param("cost") Integer cost);

    @Select("select information.id from `cq-hospital`.information join `cq-hospital`.hospitalization h on information.id = h.information_id where h.id = #{hospitalizationId}")
    Integer selectInformationIdByHospitalizationId(Integer hospitalizationId);

    @Update("update `cq-hospital`.information set total_cost = total_cost + #{cost} where id = #{informationId}")
    void updateTotalCost(Integer cost, Integer informationId);

    @Select("select information.disease, information.surgery from `cq-hospital`.information join `cq-hospital`.hospitalization h on information.id = h.information_id where h.id = #{hospitalizationId}")
    AddInformationDTO getInformationByHospitalizationId(Integer hospitalizationId);

    @Select("select * from `cq-hospital`.daily_checklist join `cq-hospital`.hospitalization h on h.information_id = daily_checklist.information_id where h.id = #{hospitalizationId}")
    List<DailyCheckList> getDailyCheckListByHospitalizationId(Integer hospitalizationId);

    @Update("update `cq-hospital`.doctor set status = status + 1 where id = #{doctorId}")
    void promoteDoctor(Integer doctorId);

    @Select("select doctor_id from `cq-hospital`.register where `order` = #{order}")
    Integer selectDoctorIdByOrder(String order);

    @Select("select * from `cq-hospital`.report where id = #{reportId}")
    Report selectReportById(Integer reportId);

    @Insert("insert into `cq-hospital`.report(report_type, patient_gender, patient_age, patient_height, patient_weight, blood_pressure, heart_rate, complete_blood_count, urinalysis, contagious_disease, information_id) " +
            "VALUES (#{reportType}, #{patientGender}, #{patientAge}, #{patientHeight}, #{patientWeight}, #{bloodPressure}, #{heartRate}, #{completeBloodCount}, #{urinalysis}, #{contagiousDisease}, #{informationId})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int dischargePatient(Report report);


    void updateInformation(Integer outReportId, String formattedDateTime, String dischargeOrders);

    //发出物流申请
    @Insert("insert into `cq-hospital`.logistics (`order`, user_name, phone, in_address, application_time, status,hospital_id,doctor_id) values (#{order}, #{userName}, #{phone}, #{inAddress}, #{applicationTime}, #{status},#{hospitalId},#{doctorId})")
    void applicationLogistics(Logistics logistics);

    @Select("select hospital_id from `cq-hospital`.doctor join `cq-hospital`.user u on u.id = doctor.user_id where u.id = #{userId}")
    Integer selectHospitalIdByDoctorId(Integer doctorId);


    List<WaitingQueuesDTO> selectWaitingQueues(@Param("doctorId") Integer doctorId, @Param("registerTime") String registerTime, @Param("date") String date);

    Integer selectQueueLength(@Param("doctorId") Integer doctorId, @Param("date") String date);
}
