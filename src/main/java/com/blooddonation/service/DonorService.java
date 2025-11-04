package com.blooddonation.service;

import com.blooddonation.model.Donor;
import com.blooddonation.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DonorService {
    
    @Autowired
    private DonorRepository donorRepository;
    
    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }
    
    public Donor saveDonor(Donor donor) {
        return donorRepository.save(donor);
    }
    
    public Map<String, Long> getBloodGroupStats() {
        return donorRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                Donor::getBloodGroup,
                Collectors.counting()
            ));
    }
    
    public List<Donor> searchDonors(String query) {
        String searchTerm = query.toUpperCase();
        return donorRepository.findAll().stream()
            .filter(donor -> 
                donor.getName().toUpperCase().contains(searchTerm) ||
                donor.getBloodGroup().toUpperCase().contains(searchTerm) ||
                donor.getLocation().toUpperCase().contains(searchTerm) ||
                donor.getPhone().contains(searchTerm))
            .collect(Collectors.toList());
    }
}