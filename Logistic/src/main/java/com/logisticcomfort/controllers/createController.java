package com.logisticcomfort.controllers;

import com.logisticcomfort.model.*;
import com.logisticcomfort.repos.*;
import com.logisticcomfort.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;

@Controller
@RequestMapping("/create")
public class createController {

    private static final Logger LOG_CREATE = LoggerFactory.getLogger(createController.class.getName());

    private final CompanyRepo companyRepo;
    private final UserRepo userRepo;
    private final WarehouseRepo warehouseRepo;
    private final ProductService productService;
    private final ProductService productRepo;
    private UserService userService;

    @Autowired
    public createController(CompanyRepo companyRepo,
                            UserRepo userRepo,
                            WarehouseRepo warehouseRepo,
                            ProductService productService, ProductService productRepo, UserService userService) {
        this.companyRepo = companyRepo;
        this.userRepo = userRepo;
        this.warehouseRepo = warehouseRepo;
        this.productService = productService;
        this.productRepo = productRepo;
        this.userService = userService;
    }

    @GetMapping("/company")
    public String createCompany(Model model,
                                @AuthenticationPrincipal User user){
        if (user.getCompany() != null) {
            LOG_CREATE.info("GetMapping - COMPANY is not NULL");
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

        var set = new HashSet<User>();
        set.add(user);

        company.setId(companyRepo.count() + 1);
        company.setAuthor(set);
        user.setCompany(company);

        userRepo.save(user);

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

        var company = userService.findCompanyByUser(user);
        company.addAWarehouse(warehouse);

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

//        product.setWarehouse(warehouse);
//        warehouse.addProducts(product);
//
//        warehouseRepo.save(warehouse);
//        productService.saveProduct(product, warehouse);

        productService.addProductInApply(product, warehouse, companyRepo.findById((long)user.getCompany().getId()));

        return "redirect:/warehouses/" + String.valueOf(id);
    }
}
