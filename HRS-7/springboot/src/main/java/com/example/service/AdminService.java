package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.DTO.LeaveApplicationDTO;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.AdminMapper;
import com.example.mapper.HospitalMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员业务处理
 **/
@Service
public class AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private HospitalMapper hospitalMapper;

    /**
     * 新增
     */
    public void add(Admin admin) {
        Admin dbAdmin = adminMapper.selectByUsername(admin.getUsername());
        if (ObjectUtil.isNotNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(admin.getPassword())) {
            admin.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(admin.getName())) {
            admin.setName(admin.getUsername());
        }
        admin.setRole(RoleEnum.ADMIN.name());
        adminMapper.insert(admin);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        adminMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            adminMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Admin admin) {
        adminMapper.updateById(admin);
    }

    /**
     * 根据ID查询
     */
    public Admin selectById(Integer id) {
        return adminMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Admin> selectAll(Admin admin) {
        return adminMapper.selectAll(admin);
    }

    /**
     * 分页查询
     */
    public PageInfo<Admin> selectPage(Admin admin, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> list = adminMapper.selectAll(admin);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Account login(Account account) {
        Account dbAdmin = adminMapper.selectByAccount(account.getAccount());
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbAdmin.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = null;
        if(dbAdmin.getRole().equals(RoleEnum.HospitalADMIN.name())){
            tokenData = dbAdmin.getId() + "-" + RoleEnum.HospitalADMIN.name();
        }else if(dbAdmin.getRole().equals(RoleEnum.ADMIN.name())){
            tokenData = dbAdmin.getId() + "-" + RoleEnum.ADMIN.name();
        }
        String token = TokenUtils.createToken(tokenData, dbAdmin.getPassword());
        dbAdmin.setToken(token);
        return dbAdmin;
    }

    /**
     * 注册
     */
    public void register(Account account) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(account, admin);
        add(admin);
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        Admin dbAdmin = adminMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbAdmin.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbAdmin.setPassword(account.getNewPassword());
        adminMapper.updateById(dbAdmin);
    }

    //循环查找医院的医生Id
    @Transactional
    public void insertWorkloadStatistics(Integer adminId, String yearMonth){
        adminMapper.selectAllDoctorId(adminId).forEach(doctorId -> {
          Integer receivedPatientCount = adminMapper.selectReceivedPatientCount(doctorId,yearMonth);
          Integer leaveApplicationCount = adminMapper.selectLeaveApplicationCount(doctorId,yearMonth);
          adminMapper.InsertWorkloadStatistics(doctorId,yearMonth,receivedPatientCount,leaveApplicationCount);
        });
    }

    @Transactional
    public void calculatePerformance(Integer adminId, String yearMonth){
        adminMapper.selectAllDoctorId(adminId).forEach(doctorId -> {
            adminMapper.calculatePerformance(doctorId,yearMonth);
        });
    }

    public List<Logistics> selectLogistics(Integer adminId){
        return adminMapper.selectLogistics(adminId);
    }

    public void shipLogistics(Integer orderId, String outAddress, String outName) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = currentDate.format(formatter);
        adminMapper.shipLogistics(orderId,outAddress,outName,date);
    }

    public PageInfo<RegistrationDetail> selectRegister(Integer adminId,Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<RegistrationDetail> list = adminMapper.selectRegister(adminId);
        return PageInfo.of(list);
    }

    public void addHealthArticle(HealthArticle healthArticle){
        healthArticle.setStatus(0);
        healthArticle.setViewCount(0);
        healthArticle.setLikeCount(0);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = currentDate.format(formatter);
        healthArticle.setPublishTime(date);
        Integer hospitalId = adminMapper.selectHospitalId(healthArticle.getHospitalId());
        healthArticle.setHospitalId(hospitalId);
        adminMapper.addHealthArticle(healthArticle);
    }

    public void addNotice(Notice notice){
        notice.setStatus(0);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = currentDate.format(formatter);
        notice.setPublishTime(date);
        Integer hospitalId = adminMapper.selectHospitalId(notice.getHospitalId());
        notice.setHospitalId(hospitalId);
        adminMapper.addNotice(notice);
    }

    public PageInfo<Notice> selectNotice(Integer id,Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Notice> list = adminMapper.selectNotice(id);
        return PageInfo.of(list);
    }

    public void toggleNewsStatus(Integer id) {
        adminMapper.toggleNewsStatus(id);
    }

    public PageInfo<OperationLog> selectAllOperationLog(Integer pageNum, Integer pageSize, String module, String userName) {
        PageHelper.startPage(pageNum, pageSize);
        List<OperationLog> list = adminMapper.selectAllOperationLog(module, userName);
        return PageInfo.of(list);
    }

    public void hospitalRating(Integer hospitalId) {
        //查找该医院下所有的订单数
        Integer orderCount = adminMapper.selectOrderCount(hospitalId);
        //查找该医院下所有的医生评分平均值
        Double rating = adminMapper.selectRating(hospitalId);
        if(orderCount>=1000 && orderCount < 5000 && rating>=3.5){
            adminMapper.updateHospitalLevel(hospitalId,2);
        }else if(orderCount>=5000 && orderCount < 10000 && rating>=3.5){
            adminMapper.updateHospitalLevel(hospitalId,3);
        } else if (orderCount>=10000 && orderCount < 20000 && rating>=3.5) {
            adminMapper.updateHospitalLevel(hospitalId,4);
        } else if (orderCount>=20000 && orderCount < 50000 && rating>=4) {
            adminMapper.updateHospitalLevel(hospitalId,5);
        } else if (orderCount>=50000 && orderCount < 100000 && rating>=4) {
            adminMapper.updateHospitalLevel(hospitalId,6);
        } else if (orderCount>=100000 && orderCount < 200000 && rating>=4) {
            adminMapper.updateHospitalLevel(hospitalId,7);
        } else if (orderCount>=200000 && orderCount < 500000 && rating>=4.3) {
            adminMapper.updateHospitalLevel(hospitalId,8);
        } else if (orderCount>=500000 && rating>=4.5) {
            adminMapper.updateHospitalLevel(hospitalId,9);
        }else{
            adminMapper.updateHospitalLevel(hospitalId,0);
        }
    }

    public void hospitalSpecialty(Integer hospitalId) {
        //查找该医院所有部门
        List<Department> departmentList = hospitalMapper.selectAllDepartments(hospitalId);
        Map<String,Double> departmentRatingMap = new HashMap<>();
        for (Department department : departmentList) {
            //查找该部门的医生评分平均值
            Double rating = adminMapper.selectDepartmentRating(hospitalId,department.getId());
            if (rating==null){
                rating = 0.0;
            }
            departmentRatingMap.put(department.getName(),rating);
        }
        String specialty = "";
        for (Map.Entry<String, Double> entry : departmentRatingMap.entrySet()) {
            if(entry.getValue()>=3.5){
                //更新该部门的擅长疾病
                specialty += entry.getKey()+"\n";
            }
        }
        adminMapper.updateDepartmentSpecialty(hospitalId,specialty);
    }

    public void insertWorkloadStatistics(Integer doctorId, String yearMonth, Integer receivedPatientCount, Integer leaveApplicationCount, Integer checkupCount, Integer surgeryCount) {
        adminMapper.insertWorkloadStatistics(doctorId, yearMonth, receivedPatientCount, leaveApplicationCount, checkupCount, surgeryCount);
    }

    public LeaveApplicationDTO selectWorkloadStatisticsByDoctorIdAndYearMonth(Integer doctorId, String yearMonth) {
        return adminMapper.selectWorkloadStatisticsByDoctorIdAndYearMonth(doctorId, yearMonth);
    }
}