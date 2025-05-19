package com.example.controller;

import com.example.DTO.EvaluationDTO;
import com.example.DTO.WaitingQueuesDTO;
import com.example.common.Result;
import com.example.entity.*;
import com.example.service.UserService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 管理员前端操作接口
 **/
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        userService.add(user);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        userService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        userService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody User user) {
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
            User user = userService.selectById(id);
            return Result.success(user);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(User user ) {
        List<User> list = userService.selectAll(user);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(User user,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<User> page = userService.selectPage(user, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/selectRegistrations")
    public Result selectRegistrations(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam Integer userId){
        PageInfo<RegistrationDetail> page = userService.selectRegistrationsPage(pageNum, pageSize,userId);
        return Result.success(page);
    }

    //查找个人档案: 包括个人信息和住院记录,当前为查找住院记录
    @GetMapping("/selectArchives/{userId}")
    public Result selectArchives(@PathVariable Integer userId){
        List<Hospitalization> hospitalization = userService.selectArchives(userId);
        return Result.success(hospitalization);
    }

    @GetMapping("/selectHospitalizationById/{hospitalizationId}")
    public Result selectHospitalizationById(@PathVariable Integer hospitalizationId){
        HospitalizationInformation hospitalizationInfo = userService.selectHospitalizationById(hospitalizationId);
        return Result.success(hospitalizationInfo);
    }

    @GetMapping("/selectReport/{reportId}")
    public Result selectReportById(@PathVariable Integer reportId){
        Report report = userService.selectReportById(reportId);
        return Result.success(report);
    }

    @GetMapping("/dailyCheckList/getByHospitalizationId/{id}")
    public Result getDailyCheckListByHospitalizationId(@PathVariable Integer id) {
        List<DailyCheckList> dailyCheckList = userService.getDailyCheckListByHospitalizationId(id);
        return Result.success(dailyCheckList);
    }
    //添加评价
    @PostMapping("/insertEvaluation")
    public Result insertEvaluation(@RequestBody EvaluationDTO evaluation){
        userService.insertEvaluation(evaluation);
        return Result.success();
    }
    //根据医生id查询所有评价
    @GetMapping("/evaluations/list/{doctorId}")
    public Result getEvaluationsByDoctorId(@PathVariable Integer doctorId) {
        List<EvaluationDTO> evaluationDTOS = userService.selectEvaluationsByDoctorId(doctorId);
        return Result.success(evaluationDTOS);
    }
    //根据医生id查询评价总数
    @GetMapping("/evaluations/average/{doctorId}")
    public Result getAverageEvaluationByDoctorId(@PathVariable Integer doctorId) {
        Double averageEvaluation = userService.selectAverageEvaluationByDoctorId(doctorId);
        return Result.success(averageEvaluation);
    }
    //根据就诊序列号，和登记时间，用户查询排队信息
    @GetMapping("/selectWaitingQueues")
    public Result selectWaitingQueues(@RequestParam String orderNumber,@RequestParam String registerTime){
        List<WaitingQueuesDTO> waitingQueues = userService.selectWaitingQueues(orderNumber,registerTime);
        return Result.success(waitingQueues);
    }

    @GetMapping("/register/today")
    public Result selectRegisterToday(@RequestParam Integer userId){
        Register register = userService.selectRegisterToday(userId);
        log.info("register:{}",register);
        return Result.success(register);
    }

    @GetMapping("/selectHealthArticlesPage")
    public Result electHealthArticlesPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam Integer hospitalId){
        PageInfo<HealthArticle> page = userService.selectHealthArticles(pageNum, pageSize,hospitalId);
        return Result.success(page);
    }

    @GetMapping("/getHealthArticle")
    public Result getHealthArticle(@RequestParam Integer id){
        HealthArticle healthArticle = userService.getHealthArticle(id);
        return Result.success(healthArticle);
    }

    @PostMapping("/likeArticle/{id}")
    public Result likeArticle(@PathVariable Integer id){
        userService.likeArticle(id);
        return Result.success();
    }

    @GetMapping("/getNoticePage")
    public Result getNoticePage(@RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam Integer hospitalId){
        PageInfo<Notice> page = userService.selectNotices(pageNum, pageSize,hospitalId);
        return Result.success(page);
    }

    @GetMapping("/getNotice")
    public Result getNotice(@RequestParam Integer id){
        Notice notice = userService.getNotice(id);
        return Result.success(notice);
    }
}