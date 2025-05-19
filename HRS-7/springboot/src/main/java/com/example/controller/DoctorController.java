package com.example.controller;

import com.example.DTO.AddInformationDTO;
import com.example.DTO.AutoScheduleRequest;
import com.example.DTO.ConsultationRecordDTO;

import com.example.DTO.WaitingQueuesDTO;
import com.example.common.Log;
import com.example.common.Result;
import com.example.entity.*;
import com.example.service.DoctorService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/doctor")
@RestController
@Slf4j
public class DoctorController {
    @Resource
    private DoctorService doctorService;

	@GetMapping("/selectDoc")
    public Result selectDocByHidAndDepId(@RequestParam String hospitalId, @RequestParam String departmentId) {
        //System.out.println(hospitalId + " // " + departmentId);
        List<Doctor> doctors = doctorService.selectDocByHidAndDepId(hospitalId, departmentId);
        return Result.success(doctors);
    }

    @GetMapping("/selectDocByAdminIdAndDepId")
    public Result selectDocByAdminIdAndDepId(@RequestParam String userId, @RequestParam String departmentId) {
        List<Doctor> doctors = doctorService.selectDocByAdminIdAndDepId(userId, departmentId);
        return Result.success(doctors);
    }


    @GetMapping("/selectDoctorById")
    public Result selectDoctorById(@RequestParam String doctorId) {
        Doctor doctor = doctorService.selectDoctorById(doctorId);
        return Result.success(doctor);
    }

    @GetMapping("/selectDoctorByName")
    public Result selectDoctorByName(@RequestParam String doctorName) {
        Doctor doctor = doctorService.selectDoctorByName(doctorName);
        return Result.success(doctor);
    }

    @GetMapping("/selectAppointments")
    public Result selectAppointments(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam Integer userId){
        PageInfo<RegistrationDetail> page = doctorService.selectAppointmentsPage(pageNum, pageSize,userId);
        return Result.success(page);
    }

    @GetMapping("/getConsultationRecords")
    public Result getConsultationRecords(@RequestParam String userName){
        List<Consultation> registrationDetails = doctorService.getConsultationRecords(userName);
        return Result.success(registrationDetails);
    }

    @PostMapping("/addConsultationRecord")
    public Result addConsultationRecord(@RequestBody ConsultationRecordDTO consultationRecordDTO){
        System.out.println("名字"+consultationRecordDTO.getUserName()+"内容"+consultationRecordDTO.getContent());
        Consultation consultation = doctorService.addConsultationRecord(consultationRecordDTO.getOrderId(),consultationRecordDTO.getContent());
        return Result.success(consultation);
    }

    @DeleteMapping("/deleteConsultationRecord/{recordId}")
    public Result deleteConsultationRecord(@PathVariable String recordId) {
        System.out.println("删除记录ID: " + recordId);
        doctorService.deleteConsultationRecord(recordId);
        return Result.success();
    }

    @PostMapping("/cancelConsultation/{registerId}")
    public Result cancelConsultation(@PathVariable Long registerId) {
        doctorService.cancelConsultation(registerId);
        return Result.success();
    }

    @PostMapping("/completeConsultation/{registerId}")
    public Result completeConsultation(@PathVariable Long registerId) {
        doctorService.completeConsultation(registerId);
        return Result.success();
    }

    @PostMapping("/submitOrder/{registerId}")
    public Result submitOrder(@PathVariable Long registerId) {
        doctorService.submitOrder(registerId);
        return Result.success();
    }

    @GetMapping("/list")
    public Result list(@RequestParam Integer userId , @RequestParam Integer departmentId) {
        List<Doctor> doctors = doctorService.list(userId,departmentId);
        return Result.success(doctors);
    }

    @GetMapping("/selectDoctorByUserId")
    public Result selectDoctorByUserId(@RequestParam Integer userId) {
        Doctor doctor = doctorService.selectDoctorByUserId(userId);
        return Result.success(doctor);
    }

    @GetMapping("/selectArchives/{doctorId}")
    public Result selectArchives(@PathVariable Integer doctorId){
        List<Hospitalization> hospitalization = doctorService.selectArchives(doctorId);
        return Result.success(hospitalization);
    }

    @PostMapping("/information/add")
    public Result informationAdd(@RequestBody AddInformationDTO information){
        doctorService.informationAdd(information);
        return Result.success();
    }

    @PostMapping("/daily/add")
    public Result dailyAdd(@RequestBody DailyCheckList dailyCheckList){
        log.info("添加每日检查记录：{}", dailyCheckList);
        doctorService.dailyAdd(dailyCheckList);
        return Result.success();
    }

    @GetMapping("/information/getByHospitalizationId/{hospitalizationId}")
    public Result getInformationByHospitalizationId(@PathVariable Integer hospitalizationId) {
        AddInformationDTO information = doctorService.getInformationByHospitalizationId(hospitalizationId);
        return Result.success(information);
    }

    @GetMapping("daily/getByHospitalizationId/{hospitalizationId}")
    public Result getDailyCheckListByHospitalizationId(@PathVariable Integer hospitalizationId) {
        List<DailyCheckList> dailyCheckLists = doctorService.getDailyCheckListByHospitalizationId(hospitalizationId);
        return Result.success(dailyCheckLists);
    }

    @Log(module = "医院后台管理",type = "更新", description = "医生升职")
    @PostMapping("/promote/{doctorId}")
    public Result promoteDoctor(@PathVariable Integer doctorId) {
        doctorService.promoteDoctor(doctorId);
        return Result.success();
    }

    @PostMapping("/patient/discharge")
    public Result dischargePatient(
            @RequestParam Integer reportId,
            @RequestParam String dischargeOrders) {
        doctorService.dischargePatient(reportId, dischargeOrders);
        return Result.success();
    }

    //申请物流
    @PostMapping("/applicationLogistics")
    public Result applicationLogistics(@RequestBody Logistics logistics){
        doctorService.applicationLogistics(logistics);
        return Result.success();
    }



    //根据医生Id和日期获取排队信息
    @GetMapping("/list")
    public Result getWaitingQueues(@RequestParam Integer doctorId, @RequestParam String registerTime) {
        List<WaitingQueuesDTO> waitingQueuesDTOS = doctorService.selectWaitingQueues(doctorId, registerTime);
        return Result.success(waitingQueuesDTOS);
    }
   //获取排队人数
    @GetMapping("/length")
    public Result getQueueLength(@RequestParam Integer doctorId) {
        Integer waitingLength = doctorService.selectQueueLength(doctorId);
        return Result.success(waitingLength);
    }
}
