package com.example.controller;

import com.example.DTO.PrescriptionDTO;
import com.example.DTO.PurchaseDTO;
import com.example.DTO.ResourceDTO;
import com.example.DTO.StockDTO;
import com.example.common.Log;
import com.example.common.Result;
import com.example.entity.Stock;
import com.example.entity.Supplier;
import com.example.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/stock")
public class StockController {
    @Resource
    private StockService stockService;

    @GetMapping("/selectByDocId")
    public Result selectByDocId(@RequestParam Integer doctorId) {
        List<Stock> stocks = stockService.selectByDocId(doctorId);
        return Result.success(stocks);
    }

    @Log(module = "医院后台管理",type = "查询", description = "通过类型查找库存")
    @GetMapping("/selectStockByType")
    public Result selectStockByType(@RequestParam Integer type , @RequestParam Integer hospitalAdminId) {
        List<StockDTO> stockDTOs = stockService.selectStockByType(type, hospitalAdminId);
        return Result.success(stockDTOs);
    }

    @Log(module = "医院后台管理",type = "更新", description = "更新货物状态")
    @PostMapping("/updateStatus/{id}")
    public Result updateStatus(@PathVariable Integer id) {
        stockService.updateStatus(id);
        return Result.success();
    }

    @PostMapping("/updateStatusDeactivated/{id}/{status}")
    public Result updateStatusDeactivated(@PathVariable Integer id, @PathVariable Integer status) {
        stockService.updateStatusDeactivated(id,status);
        return Result.success();
    }

    @Log(module = "医院后台管理",type = "查询", description = "查找供应商")
    @GetMapping("/selectSupplier")
    public Result selectSupplier(@RequestParam String name){
        List<Supplier> suppliers =  stockService.selectSupplier(name);
        log.info("suppliers:{}",suppliers);
        return Result.success(suppliers);
    }

    @Log(module = "医院后台管理",type = "新增", description = "进货")
    @PostMapping("/purchase")
    public Result purchase(@RequestBody PurchaseDTO purchaseDTO){
        stockService.purchase(purchaseDTO);
        return Result.success();
    }

    @Log(module = "医院后台管理",type = "查询", description = "查找库存资源")
    @GetMapping("/selectResource")
    public Result selectResource(@RequestParam Integer hospitalAdminId, @RequestParam Integer departmentId, @RequestParam Integer type){
        List<ResourceDTO> resourceDTOs = stockService.selectResource(hospitalAdminId,departmentId,type);
        return Result.success(resourceDTOs);
    }
}
