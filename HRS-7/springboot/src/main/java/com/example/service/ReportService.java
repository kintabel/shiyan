package com.example.service;

import com.example.DTO.AdmissionDTO;
import com.example.DTO.InformationDTO;
import com.example.DTO.ReportDTO;
import com.example.entity.Report;
import com.example.mapper.ReportMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {
    @Resource
    private ReportMapper reportMapper;

    public void create(ReportDTO reportDTO) {
        String order = reportDTO.getOrderId();
        reportMapper.insert(reportDTO);
        // 获取当前的日期和时间
        LocalDate currentDate = LocalDate.now();
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 格式化日期时间
        String formattedDate = currentDate.format(formatter);
        InformationDTO informationDTO = new InformationDTO();
        informationDTO.setInReportId(reportDTO.getId());
        informationDTO.setAdmissionTime(formattedDate);
        reportMapper.insertInformation(informationDTO);
        Integer informationId = informationDTO.getId();
        reportMapper.insertHospitalization(order, informationId);
        reportMapper.updateReport(reportDTO.getId(),informationId);
        reportMapper.updateStatus(order);
    }

    public PageInfo<Report> selectReportByDoctorId(Integer pageNum, Integer pageSize,Integer doctorUserId,String reportType){
        PageHelper.startPage(pageNum, pageSize);
        List<Report> reports = reportMapper.selectReportByDoctorId(doctorUserId,reportType);
        return PageInfo.of(reports);
    }
    public List<Report> selectReportsByConditions(Integer doctorUserId, String reportType, String userName, String startDate, String endDate) {
        return reportMapper.selectReportsByConditions(doctorUserId, reportType, userName, startDate, endDate);
    }

    public void admission(AdmissionDTO admissionDTO) {
        reportMapper.admisson(admissionDTO);
    }
}
