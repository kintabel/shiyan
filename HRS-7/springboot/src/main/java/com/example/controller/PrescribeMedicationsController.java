package com.example.controller;

import com.example.DTO.PrescribeDetailDTO;
import com.example.DTO.PrescribeUserDTO;
import com.example.DTO.PrescripetionDataDTO;
import com.example.DTO.PrescriptionDTO;
import com.example.common.Result;
import com.example.entity.Register;
import com.example.entity.RegistrationDetail;
import com.example.service.PrescribeMedicationsService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/prescribe")
public class PrescribeMedicationsController {
    @Resource
    private PrescribeMedicationsService prescribeMedicationsService;

    @PostMapping("/submit")
    public Result submit(@RequestBody PrescripetionDataDTO prescripetionDataDTO) {
        List<PrescriptionDTO> prescriptionDTOS = prescripetionDataDTO.getPrescriptions();
        System.out.println(prescripetionDataDTO.getOrder());
        Integer id = prescribeMedicationsService.insertPrescriptions(prescripetionDataDTO.getOrder());
        for (PrescriptionDTO prescriptionDTO : prescriptionDTOS) {
            prescribeMedicationsService.insertPrescriptionsMid(prescriptionDTO,id);
        }
        return Result.success();
    }

    @GetMapping("/selectPrescribeDetail")
    public Result selectPrescribeDetail(@RequestParam Integer userId,@RequestParam String orderId){
        List<PrescribeDetailDTO> prescribeDetailDTOS = prescribeMedicationsService.selectPrescribe(userId , orderId);
        return Result.success(prescribeDetailDTOS);
    }

    @GetMapping("/selectPrescribe")
    public Result selectPrescribe(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam Integer userId){
        PageInfo<PrescribeUserDTO> page = prescribeMedicationsService.selectPrescribePage(pageNum, pageSize,userId);
        return Result.success(page);
    }
}
