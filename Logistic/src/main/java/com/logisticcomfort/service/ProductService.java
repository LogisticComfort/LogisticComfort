package com.logisticcomfort.service;

import com.logisticcomfort.model.Product;
import com.logisticcomfort.repos.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepo productRepo;

    @Autowired
    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public List<Product> findAll() {
        var it = productRepo.findAll();

        var products = new ArrayList<Product>();
        for (Product e : it) {
            products.add(e);
        }

        return products;
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

    public Product saveProduct(Product product) {
        List<Product> prodTmp;
        prodTmp = findAll();
        for (Product prod : prodTmp) {
            if (prod.getVendorCode() == product.getVendorCode()) {
                long idDell = prod.getIdProduct();
                int count = product.getAmount() + prod.getAmount();
                product.setAmount(count);
                deleteById(idDell);
                productRepo.save(product);
            }
        }
        return productRepo.save(product);
    }


}
