package com.example.service;

import com.example.DTO.PrescribeDetailDTO;
import com.example.DTO.PrescribeUserDTO;
import com.example.DTO.PrescriptionDTO;
import com.example.DTO.SubPrescripetionDTO;
import com.example.entity.Doctor;
import com.example.entity.Register;
import com.example.entity.RegistrationDetail;
import com.example.mapper.DoctorMapper;
import com.example.mapper.PrescribeMedicationsMapper;
import com.example.mapper.RegisterMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PrescribeMedicationsService {
    @Resource
    private PrescribeMedicationsMapper prescribeMedicationsMapper;
    @Resource
    private RegisterMapper registerMapper;
    @Resource
    private DoctorMapper doctorMapper;

    public Integer insertPrescriptions(String order) {
        SubPrescripetionDTO subPrescripetionDTO = registerMapper.selectByOrder(order);
        subPrescripetionDTO.setOrder(order);
        // 获取当前的日期和时间
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化日期时间
        String formattedDateTime = currentDateTime.format(formatter);
        subPrescripetionDTO.setDate(formattedDateTime);
        prescribeMedicationsMapper.insertPrescriptions(subPrescripetionDTO);
        doctorMapper.prescribeMedications(order);
        return subPrescripetionDTO.getId();
    }

    public void insertPrescriptionsMid(PrescriptionDTO prescriptionDTO, Integer id) {
        String medication = prescriptionDTO.getDrugName();
        Integer number= prescriptionDTO.getQuantity();
        String content = prescriptionDTO.getInstructions();
        prescribeMedicationsMapper.insertPrescriptionsMid(medication,number,content,id);
    }

    public PageInfo<PrescribeUserDTO> selectPrescribePage(Integer pageNum, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNum, pageSize);
        Doctor doctor = doctorMapper.selectDoctorByUserId(userId);
        List<PrescribeUserDTO> prescribeUserDTOS = doctorMapper.selectRegisterByDocId(doctor.getId());
        return PageInfo.of(prescribeUserDTOS);
    }

    public List<PrescribeDetailDTO> selectPrescribe(Integer userId, String order) {
        Doctor doctor = doctorMapper.selectDoctorByUserId(userId);
        List<PrescribeDetailDTO> prescribeDetailDTOS = doctorMapper.selectPrescribe(doctor.getId(),order);
        return prescribeDetailDTOS;
    }
}
