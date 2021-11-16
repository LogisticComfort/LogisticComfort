package com.logisticcomfort.repos;

import com.logisticcomfort.model.ApplyProduct;
import com.logisticcomfort.model.Company;
import com.logisticcomfort.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface ApplyProductRepo extends CrudRepository<ApplyProduct, Long> {
    Set<ApplyProduct> findAllByCompany(Company company);
    ApplyProduct findById(long id);
}
