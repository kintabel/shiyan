package com.example.service;

import com.example.DTO.PatientAppealDTO;
import com.example.DTO.SubRegistrationDTO;
import com.example.entity.Appeal;
import com.example.entity.Doctor;
import com.example.entity.Register;
import com.example.entity.RegistrationDetail;
import com.example.mapper.FilesMapper;
import com.example.mapper.RegisterMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;
@Slf4j
@Service
public class RegisterService {
    @Resource
    private RegisterMapper registerMapper;
    @Resource
    private DoctorService doctorService;
    @Resource
    private UserService userService;
    @Resource
    private HospitalService hospitalService;

    @Resource
    private FilesMapper filesMapper;

    public Register insert(SubRegistrationDTO registration) {
        Doctor doctor = doctorService.selectDoctorById(String.valueOf(registration.getDoctorId()));
        Register register = new Register();
        String order = generateOrderNumber();
        register.setOrder(order);
        register.setDoctorId(registration.getDoctorId());
        register.setDepartmentId(doctor.getDepartmentId());
        register.setHospitalId(doctor.getHospitalId());
        register.setUserId(registration.getUserId());
        // 获取当前的日期和时间
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化日期时间
        String formattedDateTime = currentDateTime.format(formatter);
        register.setOrderTime(formattedDateTime);
        String RegisterTime = registration.getRegistrationDate() + " " + registration.getRegistrationTimeSlot();
        register.setRegisterTime(RegisterTime);
        if (registration.getRegistrationPrice() == null){
            log.error("price is null");
        }
        register.setPrice(registration.getRegistrationPrice());
        register.setStatus(0);
        register.setShiftType(registration.getShiftType());
        registerMapper.insert(register);
        return register;
    }

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

    public RegistrationDetail selectOrderDetail(Long order) {
        RegistrationDetail registrationDetail = new RegistrationDetail();
        registrationDetail = registerMapper.selectOrderDetail(order);
        return registrationDetail;
    }

    public void appeal(String orderId, String content, String fileName) {
        Integer fileId = filesMapper.selectByFileName(fileName);
        String appealOrder = "220" + generateOrderNumber();
        // 获取当前的日期和时间
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化日期时间
        String formattedDateTime = currentDateTime.format(formatter);
        Appeal appeal = new Appeal();
        appeal.setAppealOrder(appealOrder);
        appeal.setRegisterOrder(orderId);
        appeal.setContent(content);
        appeal.setFileId(fileId);
        appeal.setDate(formattedDateTime);
        appeal.setStatus(0);
        registerMapper.insertAppeal(appeal);
    }

    public PageInfo<PatientAppealDTO> patientAppealPage(Integer pageNum, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNum, pageSize);
        List<PatientAppealDTO> patientAppealDTOS = registerMapper.patientAppealPage(userId);
        return PageInfo.of(patientAppealDTOS);
    }

    public PatientAppealDTO selectByAppealOrder(String appealOrder) {
        return registerMapper.selectByAppealOrder(appealOrder);
    }

    public void handleAppeal(String appealOrder) {
        registerMapper.handleAppeal(appealOrder);
    }
}
