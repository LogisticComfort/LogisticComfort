package com.logisticcomfort.service;

import com.logisticcomfort.model.Warehouse;
import com.logisticcomfort.repos.WarehouseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseRepo warehouseRepo;

}
