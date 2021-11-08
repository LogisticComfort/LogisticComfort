package com.logisticcomfort.controllers;

import com.logisticcomfort.model.Role;
import com.logisticcomfort.model.User;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.repos.UserRepo;
import com.logisticcomfort.repos.WarehouseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class registryController {

    private final UserRepo userRepo;

    private final CompanyRepo companyRepo;

    private final WarehouseRepo warehouseRepo;

    @Autowired
    public registryController(UserRepo userRepo, CompanyRepo companyRepo, WarehouseRepo warehouseRepo) {
        this.userRepo = userRepo;
        this.companyRepo = companyRepo;
        this.warehouseRepo = warehouseRepo;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        return "authorization/registration_page";
    }

    @PostMapping("/registration")
    public String addUser(User user) {
        User userFromDb = userRepo.findByUsername(user.getUsername());

        if (userFromDb != null) {
            return "authorization/registration_page";
        }



        user.setActive(true);
        user.setRoles(Collections.singleton(Role.ADMIN));
        userRepo.save(user);

//        userRepo.save(user);

//        var set = new HashSet<User>();
//        set.add(user);
//
//        var company = new Company();
//        company.setAuthor(set);
//        company.setName("Industry");
//        company.setId((long) 2);


//        var company = companyRepo.findById(1);
//        company.addAuthor(user);
//
//        user.setCompany(company);
//        userRepo.save(user);
//        companyRepo.save(company);

        return "redirect:/login";
    }

//    @GetMapping()
//    public String main(@AuthenticationPrincipal User user){

//        if (user.getCompany() == null)
//            return "create_company";

//        var id = (long)user.getCompany().getId();
//
//        var company = companyRepo.findById(id);
//
//        for (var use: company.getAuthor()
//        ){
//            System.out.println(use.getUsername());
//        }

//        var comp = user.getCompany();
//        System.out.println("\n");
//        System.out.println(user.getUsername());
//        System.out.println(user.getCompany().getName());
//        System.out.println(comp.getName());
//        for (var user:companyRepo.findAllUsers()
//             ) {
//            System.out.println(user.getUsername());
//        }
//        return "hello";
//    }

    @GetMapping()
    public String mainPage(@AuthenticationPrincipal User user, Model model){

        if(user.getCompany() == null)
            return "redirect:/create/company";

        model.addAttribute("company", companyRepo.findById((long)user.getCompany().getId()));
        return "main";
    }

//    @GetMapping()
//    public String mainPageW(@AuthenticationPrincipal User user, Model model){
//
//        if(user.getWarehouse() == null)
//            return "redirect:/create/warehouse";
//
//        model.addAttribute("warehouse", warehouseRepo.findById((long)user.getWarehouse().getId()));
//        return "company";
//    }
}


