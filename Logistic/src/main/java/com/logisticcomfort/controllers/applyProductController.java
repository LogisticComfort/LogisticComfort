package com.logisticcomfort.controllers;

import com.logisticcomfort.model.*;
import com.logisticcomfort.repos.ApplyProductRepo;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.repos.WarehouseRepo;
import com.logisticcomfort.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/apply_products")
@PreAuthorize("hasAuthority('ADMIN')")
public class applyProductController {

    private final ProductService productService;
    private final ApplyProductRepo applyProductRepo;
    private final CompanyRepo companyRepo;
    private final WarehouseRepo warehouseRepo;

    @Autowired
    public applyProductController(ProductService productService, CompanyRepo companyRepo, ApplyProductRepo applyProductRepo, WarehouseRepo warehouseRepo) {
        this.productService = productService;
        this.companyRepo = companyRepo;
        this.applyProductRepo = applyProductRepo;
        this.warehouseRepo = warehouseRepo;
    }

    @GetMapping()
    public String showApplyProducts(@AuthenticationPrincipal User user, Model model){
        var company =  companyRepo.findById((long)user.getCompany().getId());

        model.addAttribute("company", company);
//        model.addAttribute("applyProducts", productService.findAllApplyProductsByCompany(company));
        model.addAttribute("applyProducts", productService.findAllByCompanyOrderByIdDesc(company));

        return "show_apply_products";
    }

    @PostMapping("/edit/{id}")//(Вопрос: почему полностью объект не передается)
    public String showApplyProducts(@PathVariable("id") long id, @Valid ApplyProduct applyProduct, Model model){

        var product = applyProductRepo.findById(id);
        product.setStatus(applyProduct.getStatus());
        applyProductRepo.save(product);

        if (product.getStatus() == StatusProduct.AllOWED){
            var warehouse = warehouseRepo.findById((long)product.getWarehousesId());
            var productNew = new Product(product.getName(), product.getAmountAdd(), product.getVendorCode(), warehouse);

            productService.saveProduct(productNew, warehouse);
        }
        return "redirect:/apply_products";
    }
}
