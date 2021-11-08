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

    private int amount;

    private int vendorCode;

    public Product() {
    }

    public Product(Long idProduct, String name, int amount, int vendorCode, Warehouse ware) {
        this.idProduct = idProduct;
        this.name = name;
        this.amount = amount;
        this.vendorCode = vendorCode;
        this.ware = ware;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Warehouse ware;

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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(int vendorCode) {
        this.vendorCode = vendorCode;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.ware = warehouse;
    }
    public Warehouse getWarehouse() {
        return ware;
    }

    @Override
    public String toString() {
        return "Product{" +
                "idProduct=" + idProduct +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", vendorCode=" + vendorCode +
                ", warehouse=" + ware +
                '}';
    }

}
