package com.logisticcomfort.service;

import com.logisticcomfort.model.Company;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.repos.ProductRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private static final Logger LOG_COMP_SERVICE = LoggerFactory.getLogger(CompanyService.class.getName());

    private final CompanyRepo companyRepo;

    @Autowired
    public CompanyService(ProductRepo productRepo, CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    public Company findById(long id) {
        return companyRepo.findById(id);
    }

    public void updateCompanyInfo(Company companySetUpdate, Company companyGetInfo) {
        LOG_COMP_SERVICE.info("companyGetInfo - companySetUpdate{}", companySetUpdate);
        LOG_COMP_SERVICE.info("companyGetInfo - companyGetInfo{}", companyGetInfo);

        companySetUpdate.setId(companyGetInfo.getId());
        companySetUpdate.setName(companyGetInfo.getName());
        companySetUpdate.setPhoneNumber(companyGetInfo.getPhoneNumber());
        companySetUpdate.setEmail(companyGetInfo.getEmail());
        companySetUpdate.setAddressMainOffice(companyGetInfo.getAddressMainOffice());
        companySetUpdate.setDescription(companyGetInfo.getDescription());

        LOG_COMP_SERVICE.info("company after update - companySetUpdate{}", companySetUpdate);
    }

    public Long findIdByCompany(Company company) {
        return company.getId();
    }

}
