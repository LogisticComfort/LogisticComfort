package com.logisticcomfort.controllers;

import com.logisticcomfort.model.User;
import com.logisticcomfort.service.UserService;
import com.logisticcomfort.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private UserService userService;
    private WarehouseService warehouseService;

    @Autowired
    public StaffController(UserService userService, WarehouseService warehouseService) {
        this.userService = userService;
        this.warehouseService = warehouseService;
    }

    @GetMapping()
    public String staffShow(@AuthenticationPrincipal User user, Model model){
        var company = userService.getCompany(user);
        model.addAttribute("staff", userService.findAllByCompanyOrderByIdAsc(user));
        model.addAttribute("employee", new User());
        model.addAttribute("company", company);
        model.addAttribute("warehouses", warehouseService.findAllWarehousesByCompany(company));

        return "staff/staff_panel";
    }

    @GetMapping("/show_employee")
    public String showEmployee(){
        return "show_employee";
    }

    @PostMapping("/edit/employee/{id}")
    public String editEmployee(){

        return "redirect:/staff";
    }

}
