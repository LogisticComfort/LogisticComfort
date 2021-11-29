package com.logisticcomfort.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Set;

@Entity
@Table(name = "company")
public class Company {

    @Id
    private Long id;

    @NotEmpty(message = "Company name should not be empty")
    @Size(min = 2, max = 30, message = "Company name should be between 2 and 30 characters")
    private String name;

    @OneToMany(mappedBy="company", fetch = FetchType.LAZY)
    private Set<User> author;

    @OneToMany(mappedBy="company", fetch = FetchType.EAGER)
    private Set<Warehouse> warehouses;

    @OneToMany(mappedBy="company", fetch = FetchType.LAZY)
    private Set<ApplyProduct> applyProducts;

    @NotEmpty(message = "Phone number should not be empty")
    @Size(min = 2, max = 21, message = "Phone number should be between 2 and 30 characters")
    @Pattern(regexp = "7-\\d+-\\d+-\\d+-\\d+",
            message = "введите телефон в формате 7-ххх-ххх-хх-хх")
    private String phoneNumber;

    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "Address should not be empty")
    @Size(min = 2, max = 50, message = "Address should be between 2 and 50 characters")
    private String addressMainOffice;

    @NotEmpty(message = "Description should not be empty")
    @Size(min = 2, max = 200, message = "Description should be between 2 and 200 characters")
    private String description;


    public Set<ApplyProduct> getApplyProducts() {

        return applyProducts;
    }

    public void setApplyProducts(Set<ApplyProduct> applyProducts) {
        this.applyProducts = applyProducts;
    }

    public void addApplyProducts(ApplyProduct applyProduct) {
        this.applyProducts.add(applyProduct);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddressMainOffice() {
        return addressMainOffice;
    }

    public void setAddressMainOffice(String addressMainOffice) {
        this.addressMainOffice = addressMainOffice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<User> getAuthor() {
        return author;
    }

    public void setAuthor(Set<User> author) {
        this.author = author;
    }

    public void addAuthor(User author) {
        this.author.add(author);
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

    public Set<Warehouse> getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(Set<Warehouse> warehouses) {
        this.warehouses = warehouses;
    }

    public void addAWarehouse(Warehouse warehouse) {
        this.warehouses.add(warehouse);
    }

}
