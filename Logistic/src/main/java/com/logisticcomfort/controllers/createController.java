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
    private final UserService userService;
    private final WarehouseService warehouseService;

    @Autowired
    public createController(CompanyRepo companyRepo,
                            UserRepo userRepo,
                            WarehouseRepo warehouseRepo,
                            ProductService productService,
                            ProductService productRepo,
                            UserService userService,
                            WarehouseService warehouseService) {
        this.companyRepo = companyRepo;
        this.userRepo = userRepo;
        this.warehouseRepo = warehouseRepo;
        this.productService = productService;
        this.productRepo = productRepo;
        this.userService = userService;
        this.warehouseService = warehouseService;
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

        if (user.getCompany() != null)
            return "redirect:/";


        var set = new HashSet<User>();
        set.add(user);

        company.setId(companyRepo.count() + 1);
        company.setAuthor(set);
        user.setCompany(company);

        companyRepo.save(company);
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

        if(user.getRole() != Role.ADMIN)
            return "redirect:/";

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

    @PostMapping("/employee")
    public String CreateEmployee(@ModelAttribute("employee") @Valid User user,
                                 BindingResult bindingResult,
                                 @ModelAttribute("warehouseForEmployee") @Valid Long warehouseId,
                                 Model model,
                                 @AuthenticationPrincipal User userAuth){

        if(bindingResult.hasErrors())
            return "redirect:/staff";

        if(userAuth.getRole() != Role.ADMIN)
            return "redirect:/";

        if(!userService.usersWithThisUsername(user.getUsername()))
            return "redirect:/staff";

        user.setActive(true);
        user.setCompany(userService.getCompany(userAuth));

        if(warehouseId != null && warehouseId >= 0){
            user.setWarehouse(warehouseRepo.findById((long)warehouseId));
        }

        userRepo.saveAndFlush(user);

        return "redirect:/staff";
    }
}
