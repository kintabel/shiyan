package com.example.controller;

import com.example.DTO.LeaveApplicationDTO;
import com.example.common.Log;
import com.example.common.Result;
import com.example.entity.LeaveApplication;
import com.example.entity.Schedule;
import com.example.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    @Resource
    private ScheduleService scheduleService;
    @Log(module = "医院后台管理",type = "新增", description = "新增排班")
    @PostMapping
    public Result addSchedule(@RequestBody Schedule schedule) {
        log.info("排班信息");
        log.info(String.valueOf(schedule.getHospitalId()));
        int result = scheduleService.addSchedule(schedule);
        if (result > 0) {
            return Result.success("排班添加成功");
        }
        return Result.error("500", "排班添加失败");
    }

    @Log(module = "医院后台管理",type = "查询", description = "获取医院的排班列表")
    @GetMapping("/hospital/{hospitalId}")
    public Result getSchedulesByHospital(
            @PathVariable int userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Schedule> schedules = scheduleService.getSchedulesByHospital(userId, startDate, endDate);
        return Result.success(schedules);
    }

    @Log(module = "医院后台管理",type = "查询", description = "获取部门的排班列表 ")
    @GetMapping("/department/{departmentId}")
    public Result getSchedulesByDepartment(
            @PathVariable int departmentId,
            @RequestParam int userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Schedule> schedules = scheduleService.getSchedulesByDepartment(
                userId, departmentId, startDate, endDate);
        return Result.success(schedules);
    }

    @Log(module = "医院后台管理",type = "查询", description = "获取所有医生排班信息")
    @GetMapping("/departmentByDoc/{departmentId}")
    public Result getSchedulesAndDocByDepartment(
            @PathVariable int departmentId,
            @RequestParam int hospitalId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Schedule> schedules = scheduleService.getSchedulesAndDocByDepartment(
                hospitalId, departmentId, startDate, endDate);
        return Result.success(schedules);
    }

    @Log(module = "医院后台管理",type = "删除", description = "删除排班")
    @DeleteMapping("/{id}")
    public Result deleteSchedule(@PathVariable int id) {
        int result = scheduleService.deleteSchedule(id);
        if (result > 0) {
            return Result.success("排班删除成功");
        }
        return Result.error("500", "排班删除失败");
    }

    @Log(module = "医院后台管理",type = "更新", description = "修改排班")
    @PutMapping
    public Result updateSchedule(@RequestBody Schedule schedule) {
        int result = scheduleService.updateSchedule(schedule);
        if (result > 0) {
            return Result.success("排班更新成功");
        }
        return Result.error("500", "排班更新失败");
    }

    @GetMapping("/doctor/{doctorId}")
    public Result getSchedulesByDoctor(
            @PathVariable int doctorId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        log.info("传递的开始时间：{}",startDate);
        log.info("传递的结束时间：{}",endDate);
        List<Schedule> schedules = scheduleService.getSchedulesByDoctor(doctorId, startDate, endDate);
        return Result.success(schedules);
    }
    @GetMapping("/scheduleByDoc/{doctorId}")
    public Result getScheduleByDoctorId(
            @PathVariable int doctorId,
            @RequestParam int hospitalId,
            @RequestParam int departmentId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date scheduleDate) {
        Schedule schedule = scheduleService.getScheduleByDoctorId(doctorId, hospitalId, departmentId, scheduleDate);
        return Result.success(schedule);
    }

    @GetMapping("/scheduleByDocName/{doctorName}")
    public Result getScheduleByDoctorName(
            @PathVariable String doctorName,
            @RequestParam String hospitalName,
            @RequestParam String departmentName,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date scheduleDate) {
        Schedule schedule = scheduleService.getScheduleByDoctorName(doctorName, hospitalName, departmentName, scheduleDate);
        return Result.success(schedule);
    }

    @PostMapping("/leaveApplication")
    public Result leaveApplication(@RequestBody LeaveApplicationDTO leaveApplicationDTO) {
        log.info("leaveApplicationDTO:{}", leaveApplicationDTO);

        scheduleService.leaveApplication(leaveApplicationDTO);
        return Result.success();
    }

    @Log(module = "医院后台管理",type = "查询", description = "查看当天请假申请")
    @GetMapping("/leave/list")
    public Result getLeaveApplicationsByDoctorId(
            @RequestParam Integer hospitalAdminId,
            @RequestParam Integer departmentId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        log.info("hospitalAdminId: {}", hospitalAdminId);
        log.info("departmentId: {}", departmentId);
        log.info("startDate: {}", startDate);
        log.info("endDate: {}", endDate);

        List<LeaveApplication> leaveApplications = scheduleService.getLeaveApplicationsByDoctorId(
                hospitalAdminId, departmentId, startDate, endDate);
        log.info("leaveApplications: {}", leaveApplications);
        return Result.success(leaveApplications);
    }

    @Log(module = "医院后台管理",type = "更新 ", description = "请假处理")
    @PostMapping("/leave/approve/{id}")
    public Result approve (@PathVariable Integer id){
        scheduleService.approve(id);
        return Result.success();
    }

    @GetMapping("/leave/listAll")
    public Result getLeaveApplicationsAllByDoctorId(
            @RequestParam Integer doctorId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        log.info("startDate: {}", startDate);
        log.info("endDate: {}", endDate);
        List<LeaveApplication> leaveApplications = scheduleService.getLeaveApplicationsAllByDoctorId(
                doctorId, startDate, endDate);
        log.info("leaveApplicationsAll: {}", leaveApplications);
        return Result.success(leaveApplications);
    }
}