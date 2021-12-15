package com.logisticcomfort.repos;

import com.logisticcomfort.model.Company;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface CompanyRepo extends CrudRepository<Company, Long> {
    Company findById(long id);
    ArrayList<Company> findAll();
}
