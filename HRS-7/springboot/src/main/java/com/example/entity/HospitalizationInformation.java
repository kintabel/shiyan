package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
* 住院信息详情
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalizationInformation {
    private Integer id;
    private String userName;
    private String doctorName;
    private String hospitalName;
    private String departmentName;
    private String admissionTime;
    private Float totalCost;
    private String dischargeTime;
    private String disease;
    private String dischargeOrders;
    private Integer inReportId;
    private Integer outReportId;
    private String surgery;
    private Integer dailySituationId;
    private Integer costId;
}
