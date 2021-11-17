package com.logisticcomfort.model;

import javax.persistence.*;

@Entity
public class ApplyProduct {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private int amountAdd;

    private int vendorCode;

    private Long warehousesId;

    private String warehouseName;

    private String description;

    private StatusProduct status;

    @ManyToOne (optional=true, cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private Company company;

    public ApplyProduct() {
    }

    public ApplyProduct(Product product, Long warehousesId, String warehouseName, String description, StatusProduct status, Company company) {
        this.name = product.getName();
        this.amountAdd = product.getAmount();
        this.vendorCode = product.getVendorCode();
        this.warehousesId = warehousesId;
        this.description = description;
        this.status = status;
        this.company = company;
        this.warehouseName = warehouseName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmountAdd() {
        return amountAdd;
    }

    public void setAmountAdd(int amountAdd) {
        this.amountAdd = amountAdd;
    }

    public int getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(int vendorCode) {
        this.vendorCode = vendorCode;
    }

    public Long getWarehousesId() {
        return warehousesId;
    }

    public void setWarehousesId(Long warehousesId) {
        this.warehousesId = warehousesId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusProduct getStatus() {
        return status;
    }

    public void setStatus(StatusProduct status) {
        this.status = status;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "ApplyProduct{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amountAdd=" + amountAdd +
                ", vendorCode=" + vendorCode +
                ", warehousesId=" + warehousesId +
                ", warehouseName='" + warehouseName + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '}';
    }
}
