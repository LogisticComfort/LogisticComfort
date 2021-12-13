package com.logisticcomfort.controllers;

import com.logisticcomfort.model.Product;
import com.logisticcomfort.model.Role;
import com.logisticcomfort.model.User;
import com.logisticcomfort.model.Warehouse;
import com.logisticcomfort.repos.WarehouseRepo;
import com.logisticcomfort.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/warehouses")
public class warehouseController {

    private final UserService userService;
    private final ProductService productService;
    private final WarehouseRepo warehouseRepo;
    private final WarehouseService warehouseService;

    private Model modelPublic;

    @Autowired
    public warehouseController(UserService userService, ProductService productService, WarehouseRepo warehouseRepo, WarehouseService warehouseService) {
        this.userService = userService;
        this.productService = productService;
        this.warehouseRepo = warehouseRepo;
        this.warehouseService = warehouseService;
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

        @GetMapping("/ware_delete/{id}")
    public String deleteWarehouse(@PathVariable(value = "id", required = false) long id,
                                  @AuthenticationPrincipal User user, Model model) {
        if (user.getRole() != Role.ADMIN) {
            return "redirect:/warehouses/";
        }
            try {
                warehouseService.deleteWarehouse(id);
            } catch (Exception exception){
                modelPublic = model.addAttribute("errorNotNull", true);
                System.out.println(exception.getMessage());
            }
            return "redirect:/warehouses/";
    }

    @GetMapping("/update_ware/{id}")
    public String updateWareForm(@PathVariable(value = "id", required = false) long id,
                                 @AuthenticationPrincipal User user, Model model) {

        var warehouse = warehouseService.findWarehouseById(id);
        model.addAttribute("wareUpdate", warehouse);
        return "update_ware";
    }

    @PostMapping("/update_ware/{id}")
    public String updateWare(@PathVariable(value = "id", required = false) long id,
                             @ModelAttribute("wareUpdate") @Valid Warehouse warehouse,
                             @AuthenticationPrincipal User user) {

        if (user.getRole() != Role.ADMIN) {
            return "redirect:/warehouses/";
        }

        var ware = warehouseService.findWarehouseById(id);
        warehouseService.updateWarehouse(ware, warehouse);
        warehouseRepo.save(ware);
        return "redirect:/warehouses/";
    }
}
