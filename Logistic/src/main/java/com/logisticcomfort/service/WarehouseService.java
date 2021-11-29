package com.logisticcomfort.service;

import com.logisticcomfort.model.Company;
import com.logisticcomfort.model.Warehouse;
import com.logisticcomfort.repos.WarehouseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WarehouseService {

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

}
