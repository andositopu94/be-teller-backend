package com.multipolar.uiteller.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multipolar.uiteller.backend.model.AdminCharge;
import com.multipolar.uiteller.backend.repository.AdminChargeRepository;

@RestController
@RequestMapping("/admin")
public class AdminChargeController {
	
	@Autowired
	private AdminChargeRepository chargeRepository;

	@GetMapping("/charges")
	public List<AdminCharge> getAll() {
		return chargeRepository.findAll();
	}
	
	@PostMapping("/charge")
	public List<AdminCharge> create(@RequestBody AdminCharge charge) {
		chargeRepository.save(charge);
		
		return chargeRepository.findAll();
	}
	
	@PostMapping("/charge/{id}")
	public List<AdminCharge> update(@PathVariable Long id, @RequestBody AdminCharge charge) {
		AdminCharge old = chargeRepository.findOne(id);
		old.setCharge(charge.getCharge());		
		chargeRepository.save(old);
		
		return chargeRepository.findAll();
	}
	
	
}
