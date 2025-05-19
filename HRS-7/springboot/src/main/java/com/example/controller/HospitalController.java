package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Department;
import com.example.entity.Hospital;
import com.example.service.HospitalService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("hospital")
public class HospitalController {
    @Resource
    private HospitalService hospitalService;

    @GetMapping("/selectAllData")
    public Result getHospital(@RequestParam Integer id) {
        System.out.println("接收到的请求参数 - id: " + id); // 打印请求参数
        if (id == null) {
            return Result.error(ResultCodeEnum.valueOf("医院 ID 不能为空"));
        }
        Hospital gethospital = hospitalService.getHospital(id);
        return Result.success(gethospital);
    }

    @GetMapping("/selectPage")
    public Result selectPage(Hospital hospital,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Hospital> page = hospitalService.selectPage(hospital, pageNum, pageSize);
        return Result.success(page);
    }
    @GetMapping("/departmentList")
    public Result departmentList(@RequestParam Integer userId) {
        List<Department> departments = hospitalService.departmentList(userId);
        return Result.success(departments);
    }

    @GetMapping("/selectById/{hospitalId}")
    public Result selectById(@PathVariable Integer hospitalId) {
        Hospital hospital = hospitalService.selectById(hospitalId);
        return Result.success(hospital.getName());
    }

    @GetMapping("/selectDepById/{departmentId}")
    public Result selectDepById(@PathVariable Integer departmentId) {
        Hospital hospital = hospitalService.selectDepById(departmentId);
        return Result.success(hospital.getName());
    }

    @GetMapping("/getHospitalName")
    public Result getHospitalName(@RequestParam Integer hospitalId) {
        String hospitalName = hospitalService.getHospitalName(hospitalId);
        return Result.success(hospitalName);
    }
}
