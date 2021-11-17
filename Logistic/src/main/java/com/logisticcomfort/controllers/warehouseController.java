package com.logisticcomfort.controllers;

import com.logisticcomfort.model.Product;
import com.logisticcomfort.model.User;
import com.logisticcomfort.repos.WarehouseRepo;
import com.logisticcomfort.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/warehouses")
public class warehouseController {

    private static final Logger LOG_WH_CONTROL = LoggerFactory.getLogger(warehouseController.class.getName());

    private UserService userService;
    private ProductService productService;
    private WarehouseRepo warehouseRepo;

    private Model modelPublic;

    @Autowired
    public warehouseController(UserService userService, ProductService productService, WarehouseRepo warehouseRepo) {
        LOG_WH_CONTROL.info("Autowired warehouseController");
        this.userService = userService;
        this.productService = productService;
        this.warehouseRepo = warehouseRepo;
    }

    @GetMapping()
    public String warehousesPage(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("warehouses", userService.findAllWarehousesByUser(user));
        model.addAttribute("company", userService.findCompanyByUser(user));
        LOG_WH_CONTROL.info("GetMapping - warehousesPage - model:{}", model);
        return "warehouses";
    }

    @GetMapping("/{id}")
    public String Show (@PathVariable("id") long id, Model model, @AuthenticationPrincipal User user){
        var warehouse = warehouseRepo.findById(id);
        var products = productService.findAllProductsByWarehouse(warehouse);
        model.addAttribute("products", products);
        model.addAttribute("warehouse", warehouse);
        model.addAttribute("company", userService.findCompanyByUser(user));
        model.addAttribute("product", new Product());

        LOG_WH_CONTROL.info("GetMapping - Show - model:{}", model);

        try {
            model.addAttribute("errorNotNull", modelPublic.getAttribute("errorNotNull"));
            modelPublic = null;
            LOG_WH_CONTROL.info("modelPublic is: modelPublic:{}", (Object) null);
        }catch (Exception exception){
            LOG_WH_CONTROL.warn("can not find errorNotNull attribute", exception);
        }
        return "productShow";
    }

    @RequestMapping("/delete/product")
      public String DeleteProduct(@RequestParam(value = "id", required = false) long id,
                                  @RequestParam(value = "warehouse", required = false) long warehouseId,
                                  @AuthenticationPrincipal User user, Model model){
        try {
            productService.deleteProduct(id);
        } catch (Exception exception){
            modelPublic = model.addAttribute("errorNotNull", true);
            LOG_WH_CONTROL.info("RequestMapping - DeleteProduct - modelPublic:{}", modelPublic);
            LOG_WH_CONTROL.error("DeleteProduct Exception", exception);
        }

        return "redirect:/warehouses/" + String.valueOf(warehouseId);
    }


}
