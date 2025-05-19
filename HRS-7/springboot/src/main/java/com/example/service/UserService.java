package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.DTO.EvaluationDTO;
import com.example.DTO.WaitingQueuesDTO;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 普通用户业务处理
 **/
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private DoctorService doctorService;

    /**
     * 新增
     */
    public void add(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if (ObjectUtil.isNotNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(user.getPassword())) {
            user.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(user.getName())) {
            user.setName(user.getUsername());
        }
        user.setRole(RoleEnum.USER.name());
        userMapper.insert(user);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            userMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(User user) {
        userMapper.updateById(user);
    }

    /**
     * 根据ID查询
     */
    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    }

    /**
     * 分页查询
     */
    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll(user);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Account login(Account account) {
        Account dbUser = userMapper.selectByAccount(account.getAccount());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbUser.getId() + "-" + RoleEnum.USER.name();
        String token = TokenUtils.createToken(tokenData, dbUser.getPassword());
        dbUser.setToken(token);
        return dbUser;
    }

    /**
     * 注册
     */
    public void register(Account account) {
        User user = new User();
        BeanUtils.copyProperties(account, user);
        add(user);
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbUser.setPassword(account.getNewPassword());
        userMapper.updateById(dbUser);
    }

    public PageInfo<RegistrationDetail> selectRegistrationsPage(Integer pageNum, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNum, pageSize);
        List<RegistrationDetail> registrationDetails = userMapper.selectRegistrations(userId);
        return PageInfo.of(registrationDetails);
    }

    public List<Hospitalization> selectArchives(Integer userId) {
        //查询住院记录
       return userMapper.selectHospitalizationById(userId);
    }

    public HospitalizationInformation selectHospitalizationById(Integer hospitalizationId) {
        //查询住院信息
        return userMapper.selectHospitalizationInformationById(hospitalizationId);
    }

    public Report selectReportById(Integer reportId) {
        //查询报告
        return userMapper.selectReportById(reportId);
    }

    public List<DailyCheckList> getDailyCheckListByHospitalizationId(Integer id) {
        return userMapper.getDailyCheckListByHospitalizationId(id);
    }



    public List<WaitingQueuesDTO> selectWaitingQueues(String order,String registerTime) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        Integer doctorId = doctorService.selectDoctorIdByOrder(order);
        return userMapper.selectWaitingQueues(doctorId,registerTime,formattedDate);
    }

    public Register selectRegisterToday(Integer userId) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        return userMapper.selectRegisterToday(userId,formattedDate);
    }

    public PageInfo<HealthArticle> selectHealthArticles(Integer pageNum, Integer pageSize,Integer hospitalId){
        PageHelper.startPage(pageNum, pageSize);
        List<HealthArticle> healthArticles = userMapper.selectHealthArticle(hospitalId);
        return PageInfo.of(healthArticles);
    }

    public HealthArticle getHealthArticle(Integer articleId) {
        userMapper.updateViews(articleId);
        return userMapper.getHealthArticle(articleId);
    }

    public void likeArticle(Integer id) {
        userMapper.likeArticle(id);
    }

    public PageInfo<Notice> selectNotices(Integer pageNum, Integer pageSize,Integer hospitalId){
        PageHelper.startPage(pageNum, pageSize);
        List<Notice> notices = userMapper.selectNotice(hospitalId);
        return PageInfo.of(notices);
    }

    public Notice getNotice(Integer id) {
        return userMapper.getNotice(id);
    }
   //添加评价
    public void insertEvaluation(EvaluationDTO evaluation) {
        userMapper.insertEvaluation(evaluation);
    }
    //查询评价
    public List<EvaluationDTO> selectEvaluationsByDoctorId(Integer doctorId) {
        return userMapper.selectEvaluationsByDoctorId(doctorId);
    }
    //查询医生的平均评价
    public Double selectAverageEvaluationByDoctorId(Integer doctorId) {
        return userMapper.selectAverageEvaluationByDoctorId(doctorId);
    }
}