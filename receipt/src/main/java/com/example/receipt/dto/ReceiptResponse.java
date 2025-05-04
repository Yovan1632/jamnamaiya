package com.example.receipt.dto;

import java.time.LocalDateTime;

public class ReceiptResponse {
    private Long id;
    private String challanNo;
    private String name;
    private String asnCode;
    private String scnCode;
    private String vehicleNo;
    private Integer iceSlabs;
    private String orderNo;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChallanNo() {
        return challanNo;
    }

    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAsnCode() {
        return asnCode;
    }

    public void setAsnCode(String asnCode) {
        this.asnCode = asnCode;
    }

    public String getScnCode() {
        return scnCode;
    }

    public void setScnCode(String scnCode) {
        this.scnCode = scnCode;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public Integer getIceSlabs() {
        return iceSlabs;
    }

    public void setIceSlabs(Integer iceSlabs) {
        this.iceSlabs = iceSlabs;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}