package com.example.service;

import com.example.DTO.LeaveApplicationDTO;
import com.example.entity.Doctor;
import com.example.entity.LeaveApplication;
import com.example.entity.Schedule;
import com.example.mapper.DoctorMapper;
import com.example.mapper.HospitalMapper;
import com.example.mapper.ScheduleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ScheduleService {

    @Resource
    private ScheduleMapper scheduleMapper;
    @Resource
    private HospitalMapper hospitalMapper;

    @Resource
    private DoctorMapper doctorMapper;

    public int addSchedule(Schedule schedule) {
        Integer userId = schedule.getHospitalId();
        Integer hospitalId = hospitalMapper.getHospitalById(userId);
        schedule.setHospitalId(hospitalId);
        LocalDateTime now = LocalDateTime.now();
        schedule.setCreateTime(now);
        return scheduleMapper.insertSchedule(schedule);
    }

    public List<Schedule> getSchedulesByHospital(int userId, Date startDate, Date endDate) {
        int hospitalId = hospitalMapper.getHospitalById(userId);
        return scheduleMapper.getSchedulesByHospitalAndDateRange(hospitalId, startDate, endDate);
    }

    public List<Schedule> getSchedulesByDepartment(int userId, int departmentId, Date startDate, Date endDate) {
        int hospitalId = hospitalMapper.getHospitalById(userId);
        return scheduleMapper.getSchedulesByHospitalAndDepartment(hospitalId, departmentId, startDate, endDate);
    }

    public int deleteSchedule(int id) {
        return scheduleMapper.deleteSchedule(id);
    }

    public int updateSchedule(Schedule schedule) {
        return scheduleMapper.updateSchedule(schedule);
    }

    public List<Schedule> getSchedulesByDoctor(int doctorId, Date startDate, Date endDate) {
        return scheduleMapper.getSchedulesByDoctor(doctorId, startDate, endDate);
    }

    public Schedule getScheduleByDoctorId(int doctorId, int hospitalId, int departmentId, Date scheduleDate) {
        return scheduleMapper.getScheduleByDoctorId(doctorId, hospitalId, departmentId, scheduleDate);
    }

    public Schedule getScheduleByDoctorName(String doctorName, String hospitalName, String departmentName, Date scheduleDate) {
        return scheduleMapper.getScheduleByDoctorName(doctorName, hospitalName, departmentName, scheduleDate);
    }

    public List<Schedule> getSchedulesAndDocByDepartment(int hospitalId, int departmentId, Date startDate, Date endDate) {
        return scheduleMapper.getSchedulesByHospitalAndDepartment(hospitalId, departmentId, startDate, endDate);
    }

    public void leaveApplication(LeaveApplicationDTO leaveApplicationDTO) {
        LeaveApplication leaveApplication = new LeaveApplication();
        leaveApplication.setDoctorId(leaveApplicationDTO.getDoctorId());
        leaveApplication.setLeaveType(leaveApplicationDTO.getLeaveType());
        leaveApplication.setLeaveDate(leaveApplicationDTO.getScheduleDate());
        leaveApplication.setReason(leaveApplicationDTO.getReason());
        leaveApplication.setApprovalStatus(0);//0为未审批
        leaveApplication.setApplicationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Doctor doctor = doctorMapper.selectDoctorById(String.valueOf(leaveApplicationDTO.getDoctorId()));

        leaveApplication.setHospitalId(doctor.getHospitalId());
        leaveApplication.setDepartmentId(doctor.getDepartmentId());
        scheduleMapper.leaveApplication(leaveApplication);
    }

    public List<LeaveApplication> getLeaveApplicationsByDoctorId(Integer hospitalAdminId, Integer departmentId, Date startDate, Date endDate) {
        return scheduleMapper.getLeaveApplicationsByDoctorId(hospitalAdminId, departmentId, startDate, endDate);
    }

    public List<LeaveApplication> getLeaveApplicationsAllByDoctorId(Integer doctorId, Date startDate, Date endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return scheduleMapper.getLeaveApplicationsAllByDoctorId(doctorId,startDate, endDate);
    }

    public void approve(Integer id) {
        scheduleMapper.deleteScheduleByLeave(id);
        scheduleMapper.approve(id);
    }
}