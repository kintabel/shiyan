package com.example.mapper;

import com.example.entity.LeaveApplication;
import com.example.entity.Schedule;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface ScheduleMapper {

    @Insert("INSERT INTO `cq-hospital`.schedule (hospital_id, department_id, doctor_id, schedule_date, shift_type, fatigue_score, create_time) " +
            "VALUES (#{hospitalId}, #{departmentId}, #{doctorId}, #{scheduleDate}, #{shiftType}, #{fatigueScore}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSchedule(Schedule schedule);

    @Select("SELECT * FROM `cq-hospital`.schedule WHERE hospital_id = #{hospitalId} AND schedule_date BETWEEN #{startDate} AND #{endDate}")
    List<Schedule> getSchedulesByHospitalAndDateRange(@Param("hospitalId") int hospitalId,
                                                      @Param("startDate") Date startDate,
                                                      @Param("endDate") Date endDate);

    @Select("SELECT * FROM `cq-hospital`.schedule WHERE hospital_id = #{hospitalId} AND department_id = #{departmentId} " +
            "AND schedule_date BETWEEN #{startDate} AND #{endDate}")
    List<Schedule> getSchedulesByHospitalAndDepartment(@Param("hospitalId") int hospitalId,
                                                       @Param("departmentId") int departmentId,
                                                       @Param("startDate") Date startDate,
                                                       @Param("endDate") Date endDate);

    @Delete("DELETE FROM `cq-hospital`.schedule WHERE id = #{id}")
    int deleteSchedule(int id);

    @Update("UPDATE `cq-hospital`.schedule SET shift_type = #{shiftType}, fatigue_score = #{fatigueScore} WHERE id = #{id}")
    int updateSchedule(Schedule schedule);

    @Select("SELECT * FROM `cq-hospital`.schedule WHERE doctor_id = #{doctorId} AND schedule_date BETWEEN #{startDate} AND #{endDate}")
    List<Schedule> getSchedulesByDoctor(@Param("doctorId") int doctorId,
                                        @Param("startDate") Date startDate,
                                        @Param("endDate") Date endDate);

    @Select("SELECT * FROM `cq-hospital`.schedule WHERE doctor_id = #{doctorId} AND hospital_id = #{hospitalId} AND department_id = #{departmentId} AND schedule_date = #{scheduleDate}")
    Schedule getScheduleByDoctorId(int doctorId, int hospitalId, int departmentId, Date scheduleDate);

    Schedule getScheduleByDoctorName(String doctorName, String hospitalName, String departmentName, Date scheduleDate);


    @Insert("insert into `cq-hospital`.leave_application (doctor_id, department_id, hospital_id, leave_type, leave_date, application_date, approval_status, reason) VALUES (#{doctorId}, #{departmentId}, #{hospitalId}, #{leaveType}, #{leaveDate}, #{applicationDate}, #{approvalStatus}, #{reason})")
    void leaveApplication(LeaveApplication leaveApplication);

    List<LeaveApplication> getLeaveApplicationsByDoctorId(Integer hospitalAdminId, Integer departmentId, Date startDate, Date endDate);

    List<LeaveApplication> getLeaveApplicationsAllByDoctorId(Integer doctorId, Date startDate, Date endDate);
    @Update("update  `cq-hospital`.leave_application set approval_status = '1' where id = #{id}")
    void approve(Integer id);

    void deleteScheduleByLeave(Integer id);
}