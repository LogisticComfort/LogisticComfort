package com.logisticcomfort.controllers;

import com.logisticcomfort.model.Product;
import com.logisticcomfort.model.User;
import com.logisticcomfort.repos.WarehouseRepo;
import com.logisticcomfort.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/warehouses")
public class warehouseController {

    private UserService userService;
    private ProductService productService;
    private WarehouseRepo warehouseRepo;

    @Autowired
    public warehouseController(UserService userService, ProductService productService, WarehouseRepo warehouseRepo) {
        this.userService = userService;
        this.productService = productService;
        this.warehouseRepo = warehouseRepo;
    }

    @GetMapping()
    public String warehousesPage(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("warehouses", userService.findAllWarehousesByUser(user));
        model.addAttribute("company", userService.findCompanyByUser(user));

        return "warehouses";
    }

    @GetMapping("/{id}")
    public String Show (@PathVariable("id") int id, Model model, @AuthenticationPrincipal User user){
        var warehouse = warehouseRepo.findById(id);
        var products = productService.findAllProductsByWarehouse(warehouse);
        model.addAttribute("products", products);
        model.addAttribute("warehouse", warehouse);
        model.addAttribute("company", userService.findCompanyByUser(user));
        model.addAttribute("product", new Product());

        return "/productShow";
    }


}
