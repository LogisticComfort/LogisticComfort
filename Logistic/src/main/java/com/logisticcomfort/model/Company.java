package com.logisticcomfort.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Set;

@Entity
@Table(name = "comp")
public class Company {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

//    , mappedBy="company"
//    @JoinColumn(name = "company")
    @OneToMany(mappedBy="company", fetch = FetchType.LAZY)
    private Set<User> author;

    @OneToMany(mappedBy="comp", fetch = FetchType.LAZY)
    private Set<Warehouse> warehouses;

    @Column()
    private String phoneNumber;
//    @NotBlank(message = "Обязательное поле, введите почту")
    @Column(name = "email")
    @Email
    private String email;
//    @NotBlank(message = "Обязательное поле, введите адрес")
    @Column()
    private String addressMainOffice;
//    @NotBlank(message = "Обязательное поле, введите описание компании")
    @Column(name = "description")
    private String description;


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
