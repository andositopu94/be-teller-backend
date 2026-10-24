package com.multipolar.uiteller.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multipolar.uiteller.backend.model.Holiday;
import com.multipolar.uiteller.backend.repository.HolidayRepository;

@RestController
@RequestMapping("/admin")
public class HolidayController {
	@Autowired
	private HolidayRepository holidayRepository;

	@GetMapping("/holidays")
	public List<Holiday> getAll() {
		return holidayRepository.findAll();
	}
	
	@PostMapping("/holiday")
	public List<Holiday> create(@RequestBody Holiday holiday) {
		holidayRepository.save(holiday);
		
		return holidayRepository.findAll();
	}
	
	@PostMapping("/holiday/{id}")
	public List<Holiday> update(@PathVariable Long id, @RequestBody Holiday holiday) {
		Holiday old = holidayRepository.findOne(id);
		old.setDate(holiday.getDate());		
		holidayRepository.save(old);
		
		return holidayRepository.findAll();
	}
	
	@DeleteMapping("holiday/{id}")
	public List<Holiday> delete(@PathVariable Long id) {
		Holiday holiday = holidayRepository.findOne(id);
		holidayRepository.delete(holiday);
		
		return holidayRepository.findAll();
	}
	
	@GetMapping("holiday/{id}")
	public Holiday findById(@PathVariable Long id) {
		return holidayRepository.findOne(id);
	}
}
