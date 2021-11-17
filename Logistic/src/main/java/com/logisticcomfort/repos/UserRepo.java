package com.logisticcomfort.repos;

import com.logisticcomfort.model.Company;
import com.logisticcomfort.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);

    Set<User> findAllByCompanyOrderByIdAsc(Company company);

//    List<User> findAllByCompany(Company company);

}