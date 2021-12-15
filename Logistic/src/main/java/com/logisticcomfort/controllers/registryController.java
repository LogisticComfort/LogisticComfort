package com.logisticcomfort.controllers;

import com.logisticcomfort.model.Role;
import com.logisticcomfort.model.User;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.repos.UserRepo;
import com.logisticcomfort.repos.WarehouseRepo;
import com.logisticcomfort.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    static  final Logger LOG = LoggerFactory.getLogger(registryController.class.getName());

    private final UserRepo userRepo;

    private final CompanyRepo companyRepo;

    private final WarehouseRepo warehouseRepo;

    private final UserService userService;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public registryController(UserRepo userRepo, CompanyRepo companyRepo, WarehouseRepo warehouseRepo, UserService userService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.companyRepo = companyRepo;
        this.warehouseRepo = warehouseRepo;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("hasHumanLikeThis", false);
        model.addAttribute("user", new User());
        LOG.info("GetMapping - REGISTRATION - model:{}", model);
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

        if (bindingResult.hasErrors()) {
            LOG.error("PostMapping - incorrect data entered");
            return "authorization/registration_page";
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.ADMIN));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        LOG.info("user save - user{}", user);
        return "redirect:/login";
    }
}


