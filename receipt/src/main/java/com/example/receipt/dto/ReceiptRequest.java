package com.example.receipt.dto;

public class ReceiptRequest {
    private String asnCode;
    private String scnCode;
    private String vehicleNo;
    private int iceSlabs;
    private String orderNo;

    // Getters and Setters
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

    public int getIceSlabs() {
        return iceSlabs;
    }

    public void setIceSlabs(int iceSlabs) {
        this.iceSlabs = iceSlabs;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public String toString() {
        return "ReceiptRequest{" +
                "asnCode='" + asnCode + '\'' +
                ", scnCode='" + scnCode + '\'' +
                ", vehicleNo='" + vehicleNo + '\'' +
                ", iceSlabs=" + iceSlabs +
                ", orderNo='" + orderNo + '\'' +
                '}';
    }
} 