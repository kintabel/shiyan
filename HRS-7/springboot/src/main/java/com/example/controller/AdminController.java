package com.example.controller;

import com.example.DTO.LeaveApplicationDTO;
import com.example.common.Log;
import com.example.common.Result;
import com.example.entity.*;
import com.example.service.AdminService;
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
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Resource
    private AdminService adminService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Admin admin) {
        adminService.add(admin);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        adminService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        adminService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Admin admin) {
        adminService.updateById(admin);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Admin admin = adminService.selectById(id);
        return Result.success(admin);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Admin admin) {
        List<Admin> list = adminService.selectAll(admin);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Admin admin,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Admin> page = adminService.selectPage(admin, pageNum, pageSize);
        return Result.success(page);
    }


    @Log(module = "医院后台管理",type = "新增", description = "计算绩效")
    @PostMapping("/calculate-performance")
    public Result calculatePerformance(
            @RequestParam Integer adminId,
            @RequestParam String yearMonth) {
        adminService.calculatePerformance(adminId, yearMonth);
        return Result.success();
    }

    @Log(module = "医院后台管理",type = "查询", description = "查询物流")
    @GetMapping("/logistics")
    public Result selectLogistics(@RequestParam Integer adminId) {
        List<Logistics> logisticsList = adminService.selectLogistics(adminId);
        return Result.success(logisticsList);
    }

    @Log(module = "医院后台管理",type = " 更新", description = "物流发货")
    @PostMapping("/logistics/ship")
    public Result shipLogistics(@RequestParam Integer orderId ,@RequestParam String outAddress,@RequestParam String outName) {
        adminService.shipLogistics(orderId,outAddress,outName);
        return Result.success();
    }

    @Log(module = "医院后台管理",type = "查询", description = "查看订单信息")
    @GetMapping("/selectRegisterPage")
    public Result selectRegisterPage(Integer adminId,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<RegistrationDetail> page = adminService.selectRegister(adminId, pageNum, pageSize);
        return Result.success(page);
    }

    @Log(module = "医院后台管理",type = "新增", description = "发布健康科普")
    @PostMapping("/addHealthArticle")
    public Result addHealthArticle(@RequestBody HealthArticle healthArticle) {
        adminService.addHealthArticle(healthArticle);
        return Result.success();
    }

    @Log(module = "医院后台管理",type = "新增", description = "发布公告")
    @PostMapping("/addNotice")
    public Result addNotice(@RequestBody Notice notice) {
        log.info("notice:{}",notice);
        adminService.addNotice(notice);
        return Result.success();
    }

    @Log(module = "医院后台管理",type = "查询", description = "查询公告")
    @GetMapping("/selectNotice")
    public Result selectNotice(@RequestParam Integer id,@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Notice> noticeList = adminService.selectNotice(id,pageNum,pageSize);
        return Result.success(noticeList);
    }

    @Log(module = "医院后台管理",type = "更新", description = "隐藏或显示公告")
    @PostMapping("/toggleNewsStatus/{id}")
    public Result toggleNewsStatus(@PathVariable Integer id) {

        adminService.toggleNewsStatus(id);
        return Result.success();
    }

    /*以下为顶级管理员功能*/
    @GetMapping("/selectAllOperationLog")
    public Result selectAllOperationLog(@RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(required = false) String module,
                                        @RequestParam(required = false) String userName) {
        PageInfo<OperationLog> page = adminService.selectAllOperationLog(pageNum, pageSize,module,userName);
        return Result.success(page);
    }

    //医院评级，医院等级小于9.查询所有该医院所有医生的平均评分，与订单量，订单量大于1000&&评大于3.5，可以升级
    @PostMapping("/hospital-rating/{hospitalId}")
    public Result hospitalRating(@PathVariable Integer hospitalId) {
        adminService.hospitalRating(hospitalId);
        return Result.success();
    }

    //医院擅长疾病管理，查找该医院的所有部门对应的评级，大于3.5为该部门的擅长疾病，小于3.5为该部门的不擅长疾病
    @PostMapping("/hospital-specialty/{hospitalId}")
    public Result hospitalSpecialty(@PathVariable Integer hospitalId) {
        adminService.hospitalSpecialty(hospitalId);
        return Result.success();
    }
    //插入工作统计表
    //根据医生id，年月，计算该医生的工作量统计，并插入数据库
    @Log(module = "医院后台管理",type = "新增", description = "生成工作量统计")
    @PostMapping("/workload-statistics/insert")
    public Result generateWorkloadStatistics(
            @RequestParam Integer adminId,
            @RequestParam String yearMonth) {
        adminService.insertWorkloadStatistics(adminId, yearMonth);
        return Result.success();
    }
    //根据医生id，年月，就诊数和请假数来统计改医生的工作量
    @PostMapping("/workload-statistics/add")
    public void addWorkloadStatistics(@RequestParam Integer doctorId, @RequestParam String yearMonth, @RequestParam Integer receivedPatientCount, @RequestParam Integer leaveApplicationCount, @RequestParam Integer checkupCount, @RequestParam Integer surgeryCount) {
        adminService.insertWorkloadStatistics(doctorId, yearMonth, receivedPatientCount, leaveApplicationCount, checkupCount, surgeryCount);
    }
    //获取医生的工作量统计根据医生id和年
    @GetMapping("/get")
    public Result getWorkloadStatistics(@RequestParam Integer doctorId, @RequestParam String yearMonth) {
        LeaveApplicationDTO leaveApplicationDTO = adminService.selectWorkloadStatisticsByDoctorIdAndYearMonth(doctorId, yearMonth);
        return Result.success(leaveApplicationDTO);
    }

}