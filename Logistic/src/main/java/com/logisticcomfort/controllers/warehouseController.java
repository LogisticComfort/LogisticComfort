package com.logisticcomfort.controllers;

import com.logisticcomfort.model.Product;
import com.logisticcomfort.model.Role;
import com.logisticcomfort.model.User;
import com.logisticcomfort.repos.WarehouseRepo;
import com.logisticcomfort.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/warehouses")
public class warehouseController {

    private UserService userService;
    private ProductService productService;
    private WarehouseRepo warehouseRepo;

    private Model modelPublic;

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
    public String Show (@PathVariable("id") long id, Model model, @AuthenticationPrincipal User user){

        if ((user.getWarehouse() == null || user.getWarehouse().getId() != id) && user.getRole() != Role.ADMIN )
            return "redirect:/warehouses";

        var warehouse = warehouseRepo.findById(id);
        var products = productService.findAllProductsByWarehouse(warehouse);
        model.addAttribute("products", products);
        model.addAttribute("warehouse", warehouse);
        model.addAttribute("company", userService.findCompanyByUser(user));
        model.addAttribute("product", new Product());

        try {
            model.addAttribute("errorNotNull", modelPublic.getAttribute("errorNotNull"));
            modelPublic = null;
        }catch (Exception exception){
            System.out.println("can not find errorNotNull attribute");
        }
        return "productShow";
    }

    @RequestMapping("/delete/product")
      public String DeleteProduct(@RequestParam(value = "id", required = false) long id,
                                  @RequestParam(value = "warehouse", required = false) long warehouseId,
                                  @AuthenticationPrincipal User user, Model model){

        if (user.getRole() != Role.ADMIN)
            return "redirect:/warehouses/" + String.valueOf(warehouseId);

        try {
            productService.deleteProduct(id);
        } catch (Exception exception){
            modelPublic = model.addAttribute("errorNotNull", true);
            System.out.println(exception.getMessage());
        }

        return "redirect:/warehouses/" + String.valueOf(warehouseId);
    }
}
