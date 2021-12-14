package com.logisticcomfort.service;

import com.logisticcomfort.model.*;
import com.logisticcomfort.repos.ApplyProductRepo;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.repos.ProductRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {

    private static final Logger LOG_PROD_SERVICE = LoggerFactory.getLogger(ProductService.class.getName());

    private final ProductRepo productRepo;
    private final ApplyProductRepo applyProductRepo;
    private final CompanyRepo companyRepo;

    @Autowired
    public ProductService(ProductRepo productRepo, ApplyProductRepo applyProductRepo, CompanyRepo companyRepo) {
        this.productRepo = productRepo;
        this.applyProductRepo = applyProductRepo;
        this.companyRepo = companyRepo;
    }

    public Set<Product> findAllProductsByWarehouse(Warehouse warehouse){
        return productRepo.findAllByWarehouseOrderByVendorCodeAsc(warehouse);
//        return productRepo.findAllByWarehouse(warehouse);
    }

    public Product findById(long id) {
        return productRepo.findById(id).orElse(null);
    }

    public Long count() {
        return productRepo.count();
    }

    public void deleteById(Long idProduct) {

        productRepo.deleteById(idProduct);
    }

    public void saveProduct(Product product, Warehouse warehouse) {
        LOG_PROD_SERVICE.debug("Before save Product");
        var prodTmp = productRepo.findAllByWarehouse(warehouse);
        LOG_PROD_SERVICE.info("SET<Product> - prodTmp{}", prodTmp);

        for (Product prod : prodTmp) {
            if (Objects.equals(prod.getVendorCode(), product.getVendorCode())) {
                prod.setAmount(prod.getAmount() + product.getAmount());
                productRepo.save(prod);
                return;
            }
        }
        product.setWarehouse(warehouse);
        productRepo.save(product);
        LOG_PROD_SERVICE.debug("After save Product");
    }

    public void saveApplyProduct(ApplyProduct applyProduct){
        applyProductRepo.save(applyProduct);
    }

    public Set<ApplyProduct> findAllApplyProductsByCompany(Company company){
        return applyProductRepo.findAllByCompany(company);
    }

    public Set<ApplyProduct> findAllByCompanyOrderByIdDesc(Company company){
        return applyProductRepo.findAllByCompanyOrderByIdDesc(company);
    }

    public void deleteProduct(long id)  {

        try {
            var product = findById(id);
            LOG_PROD_SERVICE.info("Delete Product - product{}", product);
            if(product.getAmount() == 0){
                product.setWarehouse(null);
                productRepo.save(product);
                productRepo.delete(product);
                return;
            }

        } catch (Exception e) {
            LOG_PROD_SERVICE.error("Количество продукции не равно 0", e);
        }
    }

    public void addProductInApply(Product product, Warehouse warehouse, Company company){
        var applyProduct = new ApplyProduct(product, warehouse.getId(), warehouse.getName(), null, StatusProduct.EXPECTS, company);
        applyProductRepo.save(applyProduct);
        LOG_PROD_SERVICE.info("Apply Product - applyProduct:{}", applyProduct);
        company.addApplyProducts(applyProduct);
        companyRepo.save(company);
        LOG_PROD_SERVICE.info("Company save - company:{}", company);
    }

    public ApplyProduct findApplyProductById(long id){
        return applyProductRepo.findById(id);
    }

}
