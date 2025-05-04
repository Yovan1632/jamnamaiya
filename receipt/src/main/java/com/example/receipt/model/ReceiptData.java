package com.example.receipt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipts")
public class ReceiptData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "challan_no", nullable = false, unique = true)
    private String challanNo;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "asn_code")
    private String asnCode;

    @Column(name = "scn_code")
    private String scnCode;

    @Column(name = "vehicle_no")
    private String vehicleNo;

    @Column(name = "ice_slabs")
    private Integer iceSlabs;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

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