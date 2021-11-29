package com.logisticcomfort.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "warehouses")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotEmpty(message = "Warehouse's name should not be empty")
    @Size(min = 2, max = 30, message = "Warehouse's name should be between 2 and 30 characters")
    private String name;

    @NotEmpty(message = "Phone number should not be empty")
    @Size(min = 2, max = 21, message = "Phone number should be between 2 and 30 characters")
    @Pattern(regexp = "7-\\d+-\\d+-\\d+-\\d+",
            message = "введите телефон в формате 7-ххх-ххх-хх-хх")
    private String phoneNumber;

    @NotEmpty(message = "Address should not be empty")
    @Size(min = 2, max = 50, message = "Address should be between 2 and 50 characters")
    private String address;

    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    private String email;

    @ManyToOne (optional=true, cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private Company company;

    @OneToMany(mappedBy="warehouse", fetch = FetchType.EAGER)
    private Set<Product> products;

    @OneToMany(mappedBy="warehouse", fetch = FetchType.LAZY)
    private Set<User> users;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public void addProducts(Product product) {
        products.add(product);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Company getComp() {
        return company;
    }

    public void setComp(Company company) {
        this.company = company;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
