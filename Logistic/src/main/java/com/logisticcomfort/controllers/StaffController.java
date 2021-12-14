package com.logisticcomfort.controllers;

import com.logisticcomfort.model.Role;
import com.logisticcomfort.model.User;
import com.logisticcomfort.model.Warehouse;
import com.logisticcomfort.repos.UserRepo;
import com.logisticcomfort.repos.WarehouseRepo;
import com.logisticcomfort.service.UserService;
import com.logisticcomfort.service.WarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG_STAFF = LoggerFactory.getLogger(StaffController.class.getName());

    private final UserService userService;
    private final WarehouseService warehouseService;
    private final WarehouseRepo warehouseRepo;
    private final UserRepo userRepo;

    private  Model modelPublic;

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
        try {
            model.addAttribute("errorNotNull", modelPublic.getAttribute("errorNotNull"));
            modelPublic = null;
        }catch (Exception exception){
            System.out.println("can not find errorNotNull attribute");
        }
        return "staff/staff_panel";
    }

    @GetMapping("/show_employee/{id}")
    public String showEmployee(@PathVariable("id") long id, @AuthenticationPrincipal User user , Model model){
        var company = userService.getCompany(user);
        LOG_STAFF.info("Company info - company{}", company);
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
        LOG_STAFF.info("user INFO - user{}", user);
        user.editRole(role);
        LOG_STAFF.info("warehouseId is - warehouseId{}", warehouseId);
        if(warehouseId != null && warehouseId >= 0){
            user.setWarehouse(warehouseRepo.findById((long)warehouseId));
        }else{
            user.setWarehouse(null);
        }

        userRepo.saveAndFlush(user);
        LOG_STAFF.info("user save - user{}", user);
        return "redirect:/staff/show_employee/" + String.valueOf(id);
    }

    @GetMapping("/employee_delete/{id}")
    public String deleteEmployee(@PathVariable(value = "id", required = false) long id,
                                 @AuthenticationPrincipal User user, Model model) {
        LOG_STAFF.info("user ROLE - Role{}", user.getRole());
        if (user.getRole() != Role.ADMIN) {
            return "redirect:/staff/";
        }

        try {
            userService.deleteEmployee(id);
        } catch (Exception e) {
            modelPublic = model.addAttribute("errorNotNull", true);
            LOG_STAFF.info("info about modelPublic - modelPublic{}", modelPublic);
            LOG_STAFF.error("employee deletion error", e);
        }

        return "redirect:/staff/";
    }

    @GetMapping("/update_employee/{id}")
    public String updateEmpForm(@PathVariable(value = "id", required = false) long id,
                                 @AuthenticationPrincipal User user, Model model) {
        var userInfo = userService.findUserById(id);
        LOG_STAFF.info("user INFO - user{}", userInfo);
        model.addAttribute("empUpdate", userInfo);
        return "update_employee";
    }

    @PostMapping("/update_employee/{id}")
    public String updateEmp(@PathVariable(value = "id", required = false) long id,
                             @ModelAttribute("empUpdate") @Valid User UserInfo,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal User user) {
        LOG_STAFF.info("user ROLE - Role{}", user.getRole());
        if (user.getRole() != Role.ADMIN) {
            return "redirect:/warehouses/";
        }

        LOG_STAFF.info("user INFO - user{}", UserInfo);
        var userUpdate = userService.findUserById(id);
        LOG_STAFF.info("user INFO - user{}", userUpdate);
        userService.updateEmployee(userUpdate, UserInfo);
        userRepo.save(userUpdate);
        return "redirect:/staff/";
    }
}
