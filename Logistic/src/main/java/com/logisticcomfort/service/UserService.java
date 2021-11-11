package com.logisticcomfort.service;

import com.logisticcomfort.model.Company;
import com.logisticcomfort.model.User;
import com.logisticcomfort.model.Warehouse;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.repos.UserRepo;
import com.logisticcomfort.repos.WarehouseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private WarehouseRepo warehouseRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }

    public Company findCompanyByUser(User user){
        return companyRepo.findById((long)user.getCompany().getId());
    }

    public Set<Warehouse> findAllWarehousesByUser(User user){
        return warehouseRepo.findAllByCompany(findCompanyByUser(user));
    }
}
