package com.multipolar.uiteller.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multipolar.uiteller.backend.model.MpnCutOff;
import com.multipolar.uiteller.backend.repository.MpnCutOffRepository;

@RestController
@RequestMapping("/admin")
public class MpnCutOffController {
	@Autowired
	private MpnCutOffRepository cutOffRepository;

	@GetMapping("/mpn-cutoffs")
	public List<MpnCutOff> getAll() {
		return cutOffRepository.findAll();
	}
	
	@PostMapping("/mpn-cutoff")
	public List<MpnCutOff> create(@RequestBody MpnCutOff mpnCutOff) {
		String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
		mpnCutOff.setUserCreator(username);
		cutOffRepository.save(mpnCutOff);
		
		return cutOffRepository.findAll();
	}
	
	@PostMapping("/mpn-cutoff/{id}")
	public List<MpnCutOff> update(@PathVariable Long id, @RequestBody MpnCutOff cutoff) {
		MpnCutOff old = cutOffRepository.findOne(id);
		old.setCutoffDate(old.getCutoffDate());
		old.setServeDate(cutoff.getServeDate());
		old.setUserCreator(old.getUserCreator());
		cutOffRepository.save(old);
		
		return cutOffRepository.findAll();
	}
	
	@DeleteMapping("mpn-cutoff/{id}")
	public List<MpnCutOff> delete(@PathVariable Long id) {
		MpnCutOff cutoff = cutOffRepository.findOne(id);
		cutOffRepository.delete(cutoff);
		
		return cutOffRepository.findAll();
	}
	
	@GetMapping("mpn-cutoff/{id}")
	public MpnCutOff findById(@PathVariable Long id) {
		return cutOffRepository.findOne(id);
	}
}
