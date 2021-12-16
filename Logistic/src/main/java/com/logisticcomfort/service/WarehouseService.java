package com.logisticcomfort.service;

import com.logisticcomfort.model.Company;
import com.logisticcomfort.model.Warehouse;
import com.logisticcomfort.repos.WarehouseRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WarehouseService {

    private static final Logger LOG_WH_SERVICE = LoggerFactory.getLogger(WarehouseService.class.getName());

    @Autowired
    private WarehouseRepo warehouseRepo;

    public Set<Warehouse> findAllWarehousesByCompany(Company company){
        var set = warehouseRepo.findAllByCompany(company);

        var warehouse = new Warehouse();
        warehouse.setId(-1l);
        warehouse.setName("default");
        set.add(warehouse);


        return set;
    }

    public Warehouse findWarehouseById(long id){
        return warehouseRepo.findById(id);
    }

    public void deleteWarehouse(long id) {
        var warehouse = warehouseRepo.findById(id);
        if (warehouse.getProducts().size() != 0) {
            try {
                throw new Exception("Склад не пустой(Есть товары или персонал)");
            } catch (Exception e) {
                LOG_WH_SERVICE.error("Склад не пустой(Есть товары или персонал)", e);
            }
        }
        warehouse.setCompany(null);
        warehouseRepo.save(warehouse);
        warehouseRepo.deleteById(id);
    }

    public void updateWarehouse(Warehouse warehouse, Warehouse warehouseInfo) {
        warehouse.setId(warehouseInfo.getId());
        warehouse.setName(warehouseInfo.getName());
        warehouse.setPhoneNumber(warehouseInfo.getPhoneNumber());
        warehouse.setAddress(warehouseInfo.getAddress());
        warehouse.setEmail(warehouseInfo.getEmail());
    }
}
