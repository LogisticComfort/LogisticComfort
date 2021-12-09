package com.logisticcomfort.controllers;

import com.logisticcomfort.model.Role;
import com.logisticcomfort.model.User;
import com.logisticcomfort.model.Warehouse;
import com.logisticcomfort.repos.UserRepo;
import com.logisticcomfort.repos.WarehouseRepo;
import com.logisticcomfort.service.UserService;
import com.logisticcomfort.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasAuthority('ADMIN')")
public class StaffController {

    private UserService userService;
    private WarehouseService warehouseService;
    private WarehouseRepo warehouseRepo;
    private UserRepo userRepo;
    private Model modelPublic;

    @Autowired
    public StaffController(UserService userService, WarehouseService warehouseService, WarehouseRepo warehouseRepo, UserRepo userRepo) {
        this.userService = userService;
        this.warehouseService = warehouseService;
        this.warehouseRepo = warehouseRepo;
        this.userRepo = userRepo;
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

    @GetMapping("/show_employee/{id}")
    public String showEmployee(@PathVariable("id") long id, @AuthenticationPrincipal User user , Model model){
        var company = userService.getCompany(user);
        model.addAttribute("company", company);
        model.addAttribute("warehouses", warehouseService.findAllWarehousesByCompany(company));
        model.addAttribute("employee", userService.findUserById(id));

        return "staff/show_employee";
    }

    @PostMapping("/edit/employee/{id}")
    public String editEmployee(@PathVariable("id") long id,
                               @AuthenticationPrincipal User userAuth,
                               @ModelAttribute("warehouseForEmployee") @Valid Long warehouseId,
                               @ModelAttribute("status") @Valid Role role,
                               Model model){

        var user = userService.findUserById(id);
        user.editRole(role);

        if(warehouseId != null && warehouseId >= 0){
            user.setWarehouse(warehouseRepo.findById((long)warehouseId));
        }else{
            user.setWarehouse(null);
        }

        userRepo.saveAndFlush(user);
        return "redirect:/staff/show_employee/" + String.valueOf(id);
    }

    @GetMapping("/employee_delete/{id}")
    public String deleteEmployee(@PathVariable(value = "id", required = false) long id,
                                 @AuthenticationPrincipal User user, Model model) {
        if (user.getRole() != Role.ADMIN) {
            return "redirect:/staff/";
        }
        var deleteEmployee = userService.findUserById(id);
        try {
            userService.deleteEmployee(id);
        } catch (Exception e) {
            modelPublic = model.addAttribute("error", true);
            System.out.println(e.getMessage());
        }
        return "redirect:/staff/";
    }


}
