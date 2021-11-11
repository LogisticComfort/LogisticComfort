package com.logisticcomfort.controllers;

import com.logisticcomfort.model.*;
import com.logisticcomfort.repos.*;
import com.logisticcomfort.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        if (user.getCompany() != null)
            return "redirect:/";
        model.addAttribute("company", new Company());
        return "create/company";
    }

    @PostMapping("/company")
    public String Create(@ModelAttribute("company") @Valid Company company,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal User user){

        if(bindingResult.hasErrors())
            return "create/company";

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
        return "create/warehouse";
    }

    @PostMapping("/warehouse")
    public String CreateWare(@ModelAttribute("warehouse") @Valid Warehouse warehouse,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal User user){

        if (bindingResult.hasErrors())
            return "create/warehouse";

        var company = userService.findCompanyByUser(user);
        company.addAWarehouse(warehouse);

        warehouse.setComp(company);

        warehouseRepo.save(warehouse);

        companyRepo.save(company);

        return "redirect:/warehouses";
    }


//    @GetMapping("/product")
//    public String createProduct(Model model){
//        model.addAttribute("product", new Product());
//        return "create/product";
//    }

    @PostMapping("/product/{id}")
    public String CreateProduct(@PathVariable("id") int id, @ModelAttribute("product") @Valid Product product,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal User user) {
        var warehouse = warehouseRepo.findById(id);

        product.setWarehouse(warehouse);
        warehouse.addProducts(product);

        warehouseRepo.save(warehouse);
        productService.saveProduct(product, warehouse);

        return "redirect:/warehouses/" + String.valueOf(id);
    }
}
