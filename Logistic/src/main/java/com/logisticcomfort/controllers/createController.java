package com.logisticcomfort.controllers;

import com.logisticcomfort.model.*;
import com.logisticcomfort.repos.*;
import com.logisticcomfort.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/create")
public class createController {

    private static final Logger LOG_CREATE = LoggerFactory.getLogger(createController.class.getName());

    private final CompanyRepo companyRepo;
    private final UserRepo userRepo;
    private final WarehouseRepo warehouseRepo;
    private final ProductService productService;
    private final ProductService productRepo;
    private final UserService userService;
    private final WarehouseService warehouseService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public createController(CompanyRepo companyRepo,
                            UserRepo userRepo,
                            WarehouseRepo warehouseRepo,
                            ProductService productService,
                            ProductService productRepo,
                            UserService userService,
                            WarehouseService warehouseService,
                            PasswordEncoder passwordEncoder) {
        this.companyRepo = companyRepo;
        this.userRepo = userRepo;
        this.warehouseRepo = warehouseRepo;
        this.productService = productService;
        this.productRepo = productRepo;
        this.userService = userService;
        this.warehouseService = warehouseService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/company")
    public String createCompany(Model model,
                                @AuthenticationPrincipal User user){
        if (user.getCompany() != null) {
            LOG_CREATE.trace("GetMapping - COMPANY is not NULL");
            return "redirect:/";
        }
        model.addAttribute("company", new Company());
        LOG_CREATE.info("GetMapping - createCompany - model:{}", model);
        return "create/company";
    }

    @PostMapping("/company")
    public String Create(@ModelAttribute("company") @Valid Company company,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal User user){

        if(bindingResult.hasErrors()) {
            LOG_CREATE.error("Company creation error");
            return "create/company";
        }

        company.setActive(false);

        if (user.getCompany() != null) {

            return "redirect:/";
        }


        var set = new HashSet<User>();
        set.add(user);
        LOG_CREATE.info("Set user - user{}", user);

        company.setId(companyRepo.count() + 1);
        company.setAuthor(set);
        user.setCompany(company);

        companyRepo.save(company);
        LOG_CREATE.info("company create - company{}", company);

        userRepo.save(user);
        LOG_CREATE.info("user info - user{}", user);
        return "redirect:/";
    }

    @GetMapping("/warehouse")
    public String createWarehouse(Model model){
        model.addAttribute("warehouse", new Warehouse());
        LOG_CREATE.info("GetMapping - createWarehouse - model:{}", model);
        return "create/warehouse";
    }

    @PostMapping("/warehouse")
    public String CreateWare(@ModelAttribute("warehouse") @Valid Warehouse warehouse,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal User user){

        if (bindingResult.hasErrors()) {
            LOG_CREATE.error("Warehouse creation error");
            return "create/warehouse";
        }

        if(user.getRole() != Role.ADMIN) {
            LOG_CREATE.info("User Role - Role{}", user.getRole());
            return "redirect:/";
        }

        var company = userService.findCompanyByUser(user);
        LOG_CREATE.info("company - company{}", company);

        company.addAWarehouse(warehouse);
        LOG_CREATE.info("company added Warehouse - warehouse{}", warehouse);

        warehouse.setComp(company);

        warehouseRepo.save(warehouse);

        companyRepo.save(company);

        return "redirect:/warehouses";
    }

    @PostMapping("/product/{id}")
    public String CreateProduct(@PathVariable("id") int id, @ModelAttribute("product") @Valid Product product,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal User user) {
        var warehouse = warehouseRepo.findById(id);
        LOG_CREATE.info("ware info - warehouse{}", warehouse);
//        product.setWarehouse(warehouse);
//        warehouse.addProducts(product);
//
//        warehouseRepo.save(warehouse);
//        productService.saveProduct(product, warehouse);

        productService.addProductInApply(product, warehouse, companyRepo.findById((long)user.getCompany().getId()));
        LOG_CREATE.info("INFO about addProductProd - product{}", product);
        LOG_CREATE.info("INFO about addProductProd - warehouse{}", warehouse);
        LOG_CREATE.info("INFO about addProductProd - company{}", companyRepo.findById((long)user.getCompany().getId()));
        return "redirect:/warehouses/" + String.valueOf(id);
    }

    @PostMapping("/employee")
    public String CreateEmployee(@ModelAttribute("employee") @Valid User user,
                                 BindingResult bindingResult,
                                 @ModelAttribute("warehouseForEmployee") @Valid Long warehouseId,
                                 Model model,
                                 @AuthenticationPrincipal User userAuth){

        if(bindingResult.hasErrors()) {
            LOG_CREATE.error("CreateEmployee ERROR");
            return "redirect:/staff";
        }

        if(userAuth.getRole() != Role.ADMIN) {
            LOG_CREATE.info("User Role - Role{}", user.getRole());
            return "redirect:/";
        }

        if(!userService.usersWithThisUsername(user.getUsername()))
            return "redirect:/staff";

        user.setActive(true);
        user.setCompany(userService.getCompany(userAuth));

        if(warehouseId != null && warehouseId >= 0){
            user.setWarehouse(warehouseRepo.findById((long)warehouseId));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.saveAndFlush(user);
        LOG_CREATE.info("user save - user{}", user);
        return "redirect:/staff";
    }
}
