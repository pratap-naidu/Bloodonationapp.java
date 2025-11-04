package com.blooddonation.controller;

import com.blooddonation.model.Donor;
import com.blooddonation.service.DonorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DonorController {
    
    @Autowired
    private DonorService donorService;
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("donors", donorService.getAllDonors());
        model.addAttribute("stats", donorService.getBloodGroupStats());
        model.addAttribute("donor", new Donor());
        return "index";
    }
    
    @PostMapping("/donor")
    public String addDonor(@ModelAttribute Donor donor) {
        donorService.saveDonor(donor);
        return "redirect:/";
    }
    
    @GetMapping("/search")
    public String search(@RequestParam String query, Model model) {
        model.addAttribute("donors", donorService.searchDonors(query));
        model.addAttribute("stats", donorService.getBloodGroupStats());
        model.addAttribute("donor", new Donor());
        model.addAttribute("searchQuery", query);
        return "index";
    }
}