package com.logisticcomfort.model;

import javax.persistence.*;

@Entity
@Table(name = "Ware")
public class Warehouse {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String warehouseName;
    private String phoneNumber;
    private String addressWarehouse;

    @ManyToOne (optional=true, cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private Company comp;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddressWarehouse() {
        return addressWarehouse;
    }

    public void setAddressWarehouse(String addressWarehouse) {
        this.addressWarehouse = addressWarehouse;
    }

    public Company getComp() {
        return comp;
    }

    public void setComp(Company comp) {
        this.comp = comp;
    }
}
