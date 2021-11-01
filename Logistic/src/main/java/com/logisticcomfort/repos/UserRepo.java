package com.logisticcomfort.repos;

import com.logisticcomfort.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);

//    List<User> findAllByCompany(Company company);

}