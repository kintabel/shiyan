package com.example.controller;

import com.example.DTO.AdmissionDTO;
import com.example.DTO.ReportDTO;
import com.example.common.Result;
import com.example.entity.Report;
import com.example.service.ReportService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/report")
@Slf4j
public class ReportController {
    @Resource
    private ReportService reportService;

    @PostMapping("/create")
    public Result create(@RequestBody ReportDTO reportDTO) {
        reportService.create(reportDTO);
        return Result.success();
    }
    //根据医生查询报告单
    @GetMapping("/selectReportByDoctorId")
    public Result selectReportByDoctorId(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam Integer doctorUserId,
            @RequestParam String reportType){
        PageInfo<Report> reports = reportService.selectReportByDoctorId(pageNum,pageSize,doctorUserId,reportType);
        return Result.success(reports);
    }

    //格局报告单类型，医生，患者姓名，开始日期，结束日期查询报告单
    @GetMapping("/search")
    public List<Report> searchReports(@RequestParam(required = false) Integer doctorUserId,
                                      @RequestParam(required = false) String reportType,
                                      @RequestParam(required = false) String userName,
                                      @RequestParam(required = false) String startDate,
                                      @RequestParam(required = false) String endDate) {
        return reportService.selectReportsByConditions(doctorUserId, reportType, userName, startDate, endDate);
    }


    @PostMapping("/admission")
    public Result admission(@RequestBody AdmissionDTO admissionDTO){
        log.info("admissionDTO:{}",admissionDTO);
        reportService.admission(admissionDTO);
        return Result.success();
    }
}
