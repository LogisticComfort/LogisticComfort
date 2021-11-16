package com.logisticcomfort.repos;

import com.logisticcomfort.model.Product;
import com.logisticcomfort.model.Warehouse;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProductRepo extends CrudRepository<Product, Long> {
    Set<Product> findAllByWarehouse(Warehouse warehouse);

    Set<Product> findAllByWarehouseOrderByVendorCodeAsc(Warehouse warehouse);
//    Set<Product> findAllByWarehouse(Warehouse warehouse, Sort sort);
    void deleteByIdProduct(long id);
}
