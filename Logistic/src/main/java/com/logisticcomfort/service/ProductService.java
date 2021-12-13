package com.logisticcomfort.service;

import com.logisticcomfort.model.*;
import com.logisticcomfort.repos.ApplyProductRepo;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.repos.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductService {

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
        var prodTmp = productRepo.findAllByWarehouse(warehouse);
        for (Product prod : prodTmp) {
            if (prod.getVendorCode() == product.getVendorCode()) {
                prod.setAmount(prod.getAmount() + product.getAmount());
                productRepo.save(prod);
                return;
            }
        }
        product.setWarehouse(warehouse);
        productRepo.save(product);
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

    public void deleteProduct(long id) throws Exception
    {
        var product = findById(id);
        if(product.getAmount() == 0){
            product.setWarehouse(null);
            productRepo.save(product);
            productRepo.delete(product);
            return;
        }

        throw new Exception("Количество продукции не равно 0");
    }

    public void addProductInApply(Product product, Warehouse warehouse, Company company){
        var applyProduct = new ApplyProduct(product, warehouse.getId(), warehouse.getName(), null, StatusProduct.EXPECTS, company);
        applyProductRepo.save(applyProduct);
        company.addApplyProducts(applyProduct);
        companyRepo.save(company);
    }

    public ApplyProduct findApplyProductById(long id){
        return applyProductRepo.findById(id);
    }

}
