package com.logisticcomfort.controllers;

import com.logisticcomfort.model.Role;
import com.logisticcomfort.model.User;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.repos.UserRepo;
import com.logisticcomfort.repos.WarehouseRepo;
import com.logisticcomfort.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Collections;

@Controller
public class registryController {

    private final UserRepo userRepo;

    private final CompanyRepo companyRepo;

    private final WarehouseRepo warehouseRepo;

    private final UserService userService;

    @Autowired
    public registryController(UserRepo userRepo, CompanyRepo companyRepo, WarehouseRepo warehouseRepo, UserService userService) {
        this.userRepo = userRepo;
        this.companyRepo = companyRepo;
        this.warehouseRepo = warehouseRepo;
        this.userService = userService;
    }


    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("hasHumanLikeThis", false);
        model.addAttribute("user", new User());
        return "authorization/registration_page";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("user") @Valid User user,
                          BindingResult bindingResult,
                          Model model) {

        if (userRepo.findByUsername(user.getUsername()) != null) {
            model.addAttribute("hasHumanLikeThis", true);
            return "authorization/registration_page";
        }

        if (bindingResult.hasErrors())
            return "authorization/registration_page";

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.ADMIN));
        userRepo.save(user);
        return "redirect:/login";
    }

    @GetMapping()
    public String mainPage(@AuthenticationPrincipal User user, Model model){

        if(user.getCompany() == null)
            return "redirect:/create/company";

        model.addAttribute("company", userService.findCompanyByUser(user));
        return "index";
    }
}


