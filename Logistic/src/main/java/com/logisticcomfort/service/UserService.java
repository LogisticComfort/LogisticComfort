package com.logisticcomfort.service;

import com.logisticcomfort.model.Company;
import com.logisticcomfort.model.Role;
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

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private static final Logger LOG_USER_SERVICE = LoggerFactory.getLogger(UserService.class.getName());

    private final UserRepo userRepo;

    private final CompanyRepo companyRepo;

    private final WarehouseRepo warehouseRepo;

    @Autowired
    public UserService(UserRepo userRepo, CompanyRepo companyRepo, WarehouseRepo warehouseRepo) {
        this.userRepo = userRepo;
        this.companyRepo = companyRepo;
        this.warehouseRepo = warehouseRepo;
    }

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
        return companyRepo.findById((long)user.getCompany().getId());
    }

    public Set<Warehouse> findAllWarehousesByUser(User user){
        return warehouseRepo.findAllByCompany(findCompanyByUser(user));
    }

    public Set<User> findAllByCompanyOrderByIdAsc(User user){
        var company = companyRepo.findById((long)user.getCompany().getId());
        return userRepo.findAllByCompanyOrderByIdAsc(company);
    }

    public Company getCompany(User user){
        return companyRepo.findById((long)user.getCompany().getId());
    }

    public User findUserById(Long id){
        return userRepo.findById((long) id);
    }

    public User findUserByUsername(String username){
        return userRepo.findByUsername(username);
    }

    public boolean usersWithThisUsername(String username){
        return userRepo.findByUsername(username) == null;
    }

    public void saveUser(User user){
        userRepo.saveAndFlush(user);
    }

    public void deleteEmployee(long id) throws Exception {
        var user = findUserById(id);
        LOG_USER_SERVICE.info("user info - user{}", user);
        var numberUsers = user.getCompany().getAuthor().size();
        LOG_USER_SERVICE.info("User Role - Role{}", user.getRole());
        LOG_USER_SERVICE.info("Company is empty? - company{}", user.getCompany());
        var set  = (HashSet<User>)userRepo.findAllByCompanyOrderByIdAsc(user.getCompany());
        var count = 0;
        for (var member: set) {
            if (member.getRole().equals(Role.ADMIN))
                count++;
        }
        if (count > 1 || user.getRole().equals(Role.USER)) {
                userRepo.deleteById(id);
        } else {
            throw new Exception("You only have one employee left.");
        }
    }

    public void updateEmployee(User userUpdate, User userInfo) {
        userUpdate.setId(userInfo.getId());
        userUpdate.setUsername(userInfo.getUsername());
        userUpdate.setFullName(userInfo.getFullName());
        userUpdate.setPassword(userInfo.getPassword());
        userUpdate.setEmail(userInfo.getEmail());
    }


}
