package com.logisticcomfort.service;

import com.logisticcomfort.model.Product;
import com.logisticcomfort.model.Warehouse;
import com.logisticcomfort.repos.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductService {

    private final ProductRepo productRepo;

    @Autowired
    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public Set<Product> findAllProductsByWarehouse(Warehouse warehouse){
        return productRepo.findAllByWarehouse(warehouse);
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
}
