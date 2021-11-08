package com.logisticcomfort.controllers;

import com.logisticcomfort.model.Company;
//import com.logisticcomfort.model.Product;
import com.logisticcomfort.model.Product;
import com.logisticcomfort.model.User;
import com.logisticcomfort.model.Warehouse;
import com.logisticcomfort.repos.CompanyRepo;
//import com.logisticcomfort.repos.ProductRepo;
import com.logisticcomfort.repos.ProductRepo;
import com.logisticcomfort.repos.UserRepo;
import com.logisticcomfort.repos.WarehouseRepo;
import com.logisticcomfort.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @Autowired
    public createController(CompanyRepo companyRepo,
                            UserRepo userRepo,
                            WarehouseRepo warehouseRepo,
                            ProductRepo productRepo,
                            ProductService productService,
                            ProductService productRepo1) {
        this.companyRepo = companyRepo;
        this.userRepo = userRepo;
        this.warehouseRepo = warehouseRepo;
        this.productService = productService;
        this.productRepo = productRepo1;
    }

    @GetMapping("/company")
    public String createCompany(Model model){
        model.addAttribute("company", new Company());
        return "create/company";
    }

    @PostMapping("/company")
    public String Create(@ModelAttribute("company") @Valid Company company,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal User user){

//        if(bindingResult.hasErrors())
//            return "create/company";

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

        var comp = companyRepo.findById((long)user.getCompany().getId());

        var set = new HashSet<Warehouse>();
        set.add(warehouse);
        comp.setWarehouses(set);

        warehouse.setComp(comp);

        warehouseRepo.save(warehouse);

        companyRepo.save(comp);

        return "redirect:/";
    }


    @GetMapping("/product")
    public String createProduct(Model model){
        model.addAttribute("product", new Product());
        return "create/product";
    }

    @PostMapping("/product")
    public String CreateProduct(@ModelAttribute("product") @Valid Product product,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal User user) {
        var warehouse = warehouseRepo.findById((long)user.getCompany().getId());

        product.setWarehouse(warehouse);

//        warehouseRepo.save(warehouse);

        productService.saveProduct(product);
        if (product.getVendorCode() == new Product().getVendorCode()) {

        }
        return "redirect:/create/productShow";
    }

    @GetMapping("/productShow")
    public String showAll(Model model) {
        List<Product> product = productRepo.findAll();
        model.addAttribute("prod", product);
        return "/productShow";
    }

}
