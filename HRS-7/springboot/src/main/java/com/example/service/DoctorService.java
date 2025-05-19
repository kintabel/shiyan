package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.DTO.AddInformationDTO;
import com.example.DTO.WaitingQueuesDTO;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.*;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.print.Doc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class DoctorService {
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private HospitalMapper hospitalMapper;
    @Resource
    private RegisterMapper registerMapper;
    @Resource
    private ScheduleMapper scheduleMapper;

    private static final int MAX_DOCTORS_PER_DAY = 2;

    public List<Doctor> selectDocByHidAndDepId(String hid, String depId) {
        List<Doctor> doctors = doctorMapper.selectDocByHidAndDepId(hid, depId);
        return doctors;
    }

    public Doctor selectDoctorById(String doctorId) {
        Doctor doctor = doctorMapper.selectDoctorById(doctorId);
        return doctor;
    }

    public Account login(Account account) {
        Account dbUser = userMapper.selectByAccount(account.getAccount());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbUser.getId() + "-" + RoleEnum.DOCTOR.name();
        String token = TokenUtils.createToken(tokenData, dbUser.getPassword());
        dbUser.setToken(token);
        return dbUser;
    }

    public Account selectById(Integer id) {
        return userMapper.selectById(id);
    }

    public Doctor selectDoctorByUserId(Integer id) {
        return doctorMapper.selectDoctorByUserId(id);
    }

    public PageInfo<RegistrationDetail> selectAppointmentsPage(Integer pageNum, Integer pageSize, Integer id) {
        PageHelper.startPage(pageNum, pageSize);
        Doctor doctor = selectDoctorByUserId(id);
        // 获取当前日期
        LocalDate now = LocalDate.now();
        // 定义日期格式化模式，格式化
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 将当前日期按照指定格式进行格式化
        String date = now.format(formatter);
        System.out.println(date);
        List<RegistrationDetail> registrationDetails = doctorMapper.selectAppointments(doctor.getId(), date);
        return PageInfo.of(registrationDetails);
    }

    public List<Consultation> getConsultationRecords(String userName) {
        List<Consultation> consultationRecords = doctorMapper.getConsultationRecords(userName);
        return consultationRecords;
    }

    public Consultation addConsultationRecord(String order, String content) {
        Consultation consultation = new Consultation();
        Register register = registerMapper.selectOrder(order);
        consultation.setRegisterId(register.getId());
        consultation.setDoctorId(register.getDoctorId());
        consultation.setUserId(register.getUserId());
        // 获取当前的日期和时间
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化日期时间
        String formattedDateTime = currentDateTime.format(formatter);
        consultation.setConsultationDate(formattedDateTime);
        ;
        consultation.setContent(content);
        doctorMapper.addConsultationRecord(consultation);
        return consultation;
    }

    public void deleteConsultationRecord(String recordId) {
        doctorMapper.deleteConsultationRecord(recordId);
    }

    public void cancelConsultation(Long registerId) {
        doctorMapper.cancelConsultation(registerId);
    }

    public void completeConsultation(Long registerId) {
        doctorMapper.completeConsultation(registerId);
    }

    public void submitOrder(Long registerId) {
        doctorMapper.submitOrder(registerId);
    }

    public Doctor selectDoctorByName(String doctorName) {
       return doctorMapper.selectDoctorByName(doctorName);
    }



    public List<Doctor> list(Integer userId, Integer departmentId) {
        Integer hospitalId = hospitalMapper.getHospitalById(userId);
        List<Doctor> doctors = doctorMapper.selectDocByHidAndDepId(hospitalId.toString(),departmentId.toString());
        return doctors;
    }

    public List<Doctor> selectDocByAdminIdAndDepId(String userId, String departmentId) {
        Integer hospitalId = hospitalMapper.getHospitalById(Integer.valueOf(userId));
        List<Doctor> doctors = doctorMapper.selectDocByHidAndDepId(hospitalId.toString(),departmentId.toString());
        return doctors;
    }

    public List<Hospitalization> selectArchives(Integer userId) {
        Doctor doctor = selectDoctorByUserId(userId);
        return doctorMapper.selectHospitalizationById(doctor.getId());
    }

    public void informationAdd(AddInformationDTO information) {
        doctorMapper.informationAdd(information);
    }

    public void dailyAdd(DailyCheckList dailyCheckList) {
        Integer hospitalizationId = dailyCheckList.getHospitalizationIdOrinformationId();
        log.error("hospitalizationId:{}",hospitalizationId);
        Integer informationId = doctorMapper.selectInformationIdByHospitalizationId(hospitalizationId);
        log.error("informationId:{}",informationId);
        dailyCheckList.setHospitalizationIdOrinformationId(informationId);
        String date = dailyCheckList.getDate();
        String medicine = dailyCheckList.getMedicine();
        String transfusion = dailyCheckList.getTransfusion();
        String surgery = dailyCheckList.getSurgery();
        String surgeryTime = dailyCheckList.getSurgeryTime();
        String dailySituation = dailyCheckList.getDailySituation();
        Integer cost = dailyCheckList.getCost();

        doctorMapper.dailyAdd(informationId,date,medicine,transfusion,surgery,surgeryTime,dailySituation,cost);
        doctorMapper.updateTotalCost(dailyCheckList.getCost(),informationId);
    }

    public AddInformationDTO getInformationByHospitalizationId(Integer hospitalizationId) {
        return doctorMapper.getInformationByHospitalizationId(hospitalizationId);
    }

    public List<DailyCheckList> getDailyCheckListByHospitalizationId(Integer hospitalizationId) {
        return doctorMapper.getDailyCheckListByHospitalizationId(hospitalizationId);
    }

    public void promoteDoctor(Integer doctorId) {
        doctorMapper.promoteDoctor(doctorId);
    }

    public Integer selectDoctorIdByOrder(String order) {
        return doctorMapper.selectDoctorIdByOrder(order);
    }

    public void dischargePatient(Integer reportId, String dischargeOrders) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        Report report = doctorMapper.selectReportById(reportId);
        report.setReportType("出院报告");
        doctorMapper.dischargePatient(report);
        Integer outReportId = report.getId();
        doctorMapper.updateInformation(outReportId,formattedDateTime,dischargeOrders);
    }

    //申请物流
    public void applicationLogistics(Logistics logistics) {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = now.format(formatter);
        logistics.setApplicationTime(date);
        logistics.setStatus(0);
        String order = generateOrderNumber();
        logistics.setOrder(order);
        Integer hospitalId = doctorMapper.selectHospitalIdByDoctorId(logistics.getDoctorId());
        logistics.setHospitalId(hospitalId);
        doctorMapper.applicationLogistics(logistics);
    }

    //生成随机订单号
    public static String generateOrderNumber() {
        // 获取当前日期
        LocalDate now = LocalDate.now();
        // 定义日期格式化模式，格式化为 yyMMdd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        // 将当前日期按照指定格式进行格式化
        String datePart = now.format(formatter);

        // 创建 Random 对象用于生成随机数
        Random random = new Random();
        // 生成 4 位随机数，范围是 0 - 9999
        int randomNumber = random.nextInt(10000);
        // 将随机数格式化为 4 位字符串，不足 4 位时前面补 0
        String randomPart = String.format("%04d", randomNumber);

        // 拼接日期部分和随机数部分，组成 12 位订单号
        return datePart + randomPart;
    }
    public List<WaitingQueuesDTO> selectWaitingQueues(Integer doctorId, String registerTime) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        return doctorMapper.selectWaitingQueues(doctorId, registerTime, formattedDate);
    }

    public Integer selectQueueLength(Integer doctorId) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        return doctorMapper.selectQueueLength(doctorId, formattedDate);
    }
}
