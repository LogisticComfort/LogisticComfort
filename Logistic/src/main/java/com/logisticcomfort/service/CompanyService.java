package com.logisticcomfort.service;

import com.logisticcomfort.model.Company;
import com.logisticcomfort.model.Warehouse;
import com.logisticcomfort.repos.ApplyProductRepo;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.repos.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    @Autowired
    private final CompanyRepo companyRepo;

    public CompanyService(ProductRepo productRepo, ApplyProductRepo applyProductRepo, CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    public Company findById(long id) {
        return companyRepo.findById(id);
    }

    public void updateCompanyInfo(Company companySetUpdate, Company companyGetInfo) {
        companySetUpdate.setId(companyGetInfo.getId());
        companySetUpdate.setName(companyGetInfo.getName());
        companySetUpdate.setPhoneNumber(companyGetInfo.getPhoneNumber());
        companySetUpdate.setEmail(companyGetInfo.getEmail());
        companySetUpdate.setAddressMainOffice(companyGetInfo.getAddressMainOffice());
        companySetUpdate.setDescription(companyGetInfo.getDescription());
    }

    public Long findIdByCompany(Company company) {
        return company.getId();
    }
//    public void updateWarehouse(Warehouse warehouse, Warehouse warehouseInfo) {
//        warehouse.setId(warehouseInfo.getId());
//        warehouse.setName(warehouseInfo.getName());
//        warehouse.setPhoneNumber(warehouseInfo.getPhoneNumber());
//        warehouse.setAddress(warehouseInfo.getAddress());
//        warehouse.setEmail(warehouseInfo.getEmail());
//    }
}
