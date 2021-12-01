package com.logisticcomfort.model;

import javax.persistence.*;

@Entity
@Table(name = "Product")
public class Product {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idProduct;

    private String name;

    private Long amount;

    private Long vendorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    private Warehouse warehouse;

    public Product() {
    }

    public Product(String name, Long amount, Long vendorCode, Warehouse ware) {
        this.name = name;
        this.amount = amount;
        this.vendorCode = vendorCode;
        this.warehouse = ware;
    }

    public Long getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Long id) {
        this.idProduct = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(Long vendorCode) {
        this.vendorCode = vendorCode;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
    public Warehouse getWarehouse() {
        return warehouse;
    }

    @Override
    public String toString() {
        return "Product{" +
                "idProduct=" + idProduct +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", vendorCode=" + vendorCode +
                ", warehouse=" + warehouse +
                '}';
    }

}
