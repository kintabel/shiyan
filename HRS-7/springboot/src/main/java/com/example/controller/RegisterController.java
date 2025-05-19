package com.example.controller;

import com.example.DTO.AppealDTO;
import com.example.DTO.PatientAppealDTO;
import com.example.common.Log;
import com.example.common.Result;
import com.example.entity.Register;
import com.example.service.RegisterService;
import com.example.DTO.SubRegistrationDTO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
@Slf4j
@RestController
@RequestMapping("/registration")
public class RegisterController {
    @Resource
    private RegisterService registerService;
    @PostMapping("/insert")
    public Result insert(@RequestBody SubRegistrationDTO registration) {
        log.info("registration:{}",registration);
        Register register = registerService.insert(registration);
        return Result.success(register);
    }

    @GetMapping("/detail")
    public Result selectOrderDetail(@RequestParam Long orderId) {
        return Result.success(registerService.selectOrderDetail(orderId));
    }

    @PostMapping("/appeal")
    public Result appeal(@RequestBody AppealDTO appealDTO){
        String fileName = appealDTO.getFileName();
        String orderId = appealDTO.getOrderId();
        String content = appealDTO.getContent();
        registerService.appeal(orderId,content,fileName);
        return Result.success();
    }

    @GetMapping("/patient-appeal")
    public Result patientAppeal(@RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam Integer userId){
        PageInfo<PatientAppealDTO> page = registerService.patientAppealPage(pageNum, pageSize,userId);
        return Result.success(page);
    }

    @GetMapping("/selectByAppealOrder")
    public Result selectByAppealOrder(@RequestParam String appealOrder){
        PatientAppealDTO patientAppealDTO = registerService.selectByAppealOrder(appealOrder);
        System.out.println(patientAppealDTO.getFile());
        return Result.success(patientAppealDTO);
    }

    @Log(module = "医院后台管理",type = "更新", description = "处理患者申诉")
    @PostMapping("/handleAppeal/{appealOrder}")
    public Result handleAppeal(@PathVariable String appealOrder){
        registerService.handleAppeal(appealOrder);
        return Result.success();
    }

}
