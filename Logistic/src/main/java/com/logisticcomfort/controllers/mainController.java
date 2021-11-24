package com.logisticcomfort.controllers;

import com.logisticcomfort.model.User;
import com.logisticcomfort.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class mainController {

    private UserService userService;

    @Autowired
    public mainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String mainPage(@AuthenticationPrincipal User user, Model model){

        if(user.getCompany() == null)
            return "redirect:/create/company";

        model.addAttribute("company", userService.findCompanyByUser(user));
        return "index";
    }
}
