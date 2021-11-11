package com.logisticcomfort.controllers;

import com.logisticcomfort.model.User;
import com.logisticcomfort.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class simpleController {

    private UserService userService;

    @Autowired
    public simpleController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/warehouses")
    public String warehousesPage(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("warehouses", userService.findAllWarehousesByUser(user));
        model.addAttribute("company", userService.findCompanyByUser(user));

        return "warehouses";
    }
}
