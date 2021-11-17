package com.logisticcomfort.service;

import com.logisticcomfort.model.Company;
import com.logisticcomfort.model.User;
import com.logisticcomfort.model.Warehouse;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.repos.UserRepo;
import com.logisticcomfort.repos.WarehouseRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private static final Logger LOG_USER_SERVICE = LoggerFactory.getLogger(UserService.class.getName());

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private WarehouseRepo warehouseRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userRepo.findByUsername(username);
        } catch (Exception e) {
            LOG_USER_SERVICE.error("Username Not Found Exception", e);
        }
        return null;
    }

    public Company findCompanyByUser(User user){
        LOG_USER_SERVICE.debug("Method - findCompanyByUser");
        return companyRepo.findById((long)user.getCompany().getId());
    }

    public Set<Warehouse> findAllWarehousesByUser(User user){
        LOG_USER_SERVICE.debug("Method - findAllWarehousesByUser");
        return warehouseRepo.findAllByCompany(findCompanyByUser(user));
    }
}
