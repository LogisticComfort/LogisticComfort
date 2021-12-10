package com.logisticcomfort.controllers;

import com.logisticcomfort.model.Company;
import com.logisticcomfort.model.Role;
import com.logisticcomfort.model.User;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.service.CompanyService;
import com.logisticcomfort.service.UserService;
import org.apache.catalina.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class mainController {

    private final UserService userService;
    private final CompanyService companyService;
    private final CompanyRepo companyRepo;

    @Autowired
    public mainController(UserService userService, CompanyService companyService, CompanyRepo companyRepo) {
        this.userService = userService;
        this.companyService = companyService;
        this.companyRepo = companyRepo;
    }

    @GetMapping()
    public String mainPage(@AuthenticationPrincipal User user, Model model){

        if(user.getCompany() == null) {
            return "redirect:/create/company";
        }

        model.addAttribute("company", userService.findCompanyByUser(user));
        return "index";
    }

    @GetMapping("/update_company/{id}")
    public String updateCompanyInfoForm(@PathVariable(value = "id", required = false) Long id,
                                        @AuthenticationPrincipal User user, Model model) {

        var company = userService.findCompanyByUser(user);
        Long identity = companyService.findIdByCompany(company);
        model.addAttribute("companyUpdate", company);
        return "create/update_company";
    }

    @PostMapping("/update_company/{id}")
    public String updateCompanyInfo(@PathVariable(value = "id", required = false) long id,
                                    @ModelAttribute("companyUpdate") @Valid Company company,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal User user) {

        if (user.getRole() != Role.ADMIN) {
            return "index";
        }

        var companyUpdate = companyService.findById(id);
        companyService.updateCompanyInfo(companyUpdate, company);
        companyRepo.save(companyUpdate);
        return "redirect:/";
    }
}
