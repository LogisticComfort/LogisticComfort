package com.logisticcomfort.controllers;

import com.logisticcomfort.model.Company;
import com.logisticcomfort.model.User;
import com.logisticcomfort.model.Warehouse;
import com.logisticcomfort.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/menu_for_programmers")
public class ProgrammerController {

    private static CompanyService companyService;

    public ProgrammerController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("")
    public String showProgrammerMenu(@AuthenticationPrincipal User user, Model model){
        if (!user.isProgrammer())
            return "redirect:/";

        model.addAttribute("companies", companyService.findAllCompanies());
        return "programmer/show_companies";
    }

    @GetMapping("/company/edit/{id}")
    public String showCompany(@PathVariable("id") Long id, @AuthenticationPrincipal User user, Model model){
        if (!user.isProgrammer())
            return "redirect:/";

        var company = companyService.findById(id);
        model.addAttribute("company", company);
        model.addAttribute("active", company.isActive());
        return "programmer/show_company";
    }

    @PostMapping("/company/edit")
    public String editCompany( @RequestParam(value = "id", required = false) long id,
                               @ModelAttribute("active") boolean active,
                               @AuthenticationPrincipal User user){

        if (!user.isProgrammer())
            return "redirect:/";

        var company = companyService.findById(id);
        company.setActive(active);
        companyService.saveCompany(company);
        return "redirect:/menu_for_programmers";
    }
}
