package com.logisticcomfort.repos;

import com.logisticcomfort.model.Company;
import org.springframework.data.repository.CrudRepository;

public interface CompanyRepo extends CrudRepository<Company, Long> {
    Company findById(long id);
}
