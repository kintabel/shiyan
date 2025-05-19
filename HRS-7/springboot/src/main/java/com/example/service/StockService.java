package com.example.service;

import com.example.DTO.*;
import com.example.entity.Stock;
import com.example.entity.Supplier;
import com.example.mapper.StockMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class StockService {
    @Resource
    private StockMapper stockMapper;

    public List<Stock> selectByDocId(Integer doctorId) {
        return stockMapper.selectByDocId(doctorId);
    }

    public List<StockDTO> selectStockByType(Integer type, Integer hospitalAdminId) {
        return stockMapper.selectStockByType(type, hospitalAdminId);
    }

    public void updateStatus(Integer id) {
        stockMapper.updateStatus(id);
    }

    public void updateStatusDeactivated(Integer id, Integer status) {
        stockMapper.updateStatusDeactivated(id, status);
    }

    public List<Supplier> selectSupplier(String name) {
        return stockMapper.selectSupplier(name);
    }

    public void purchase(PurchaseDTO purchaseDTO) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        purchaseDTO.setDate(formattedDateTime);
        Integer totalPrice = purchaseDTO.getQuantity() * purchaseDTO.getSupplierPrice();
        purchaseDTO.setTotalPrice(totalPrice);
        Integer supplierId = stockMapper.selectSupplierByName(purchaseDTO.getSupplierName());
        stockMapper.insertPurchase(purchaseDTO.getName(), supplierId);
        stockMapper.purchase(purchaseDTO);

        log.info("purchaseDTO:{}", purchaseDTO);
        stockMapper.inserStock(purchaseDTO);
        //String order = getRandomOrder();
    }

    public List<ResourceDTO> selectResource(Integer hospitalAdminId, Integer departmentId, Integer type) {
        return stockMapper.selectResource(hospitalAdminId, departmentId, type);
    }

/*    public static String getRandomOrder(){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = currentDate.format(formatter);
        Integer order = (int) (Math.random() * 1000000);
        String orderString = String.format("%08d", order);
        String result = formattedDate + orderString;
        return result;
    }*/

}
