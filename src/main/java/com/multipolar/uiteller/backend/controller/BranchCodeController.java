package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.BranchCode;
import com.multipolar.uiteller.backend.repository.BranchCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class BranchCodeController {
	
	@Autowired
	private BranchCodeRepository codeRepository;

	@GetMapping("/codes")
	public List<BranchCode> getAll() {
		return codeRepository.findAll();
	}
	
	@PostMapping("/code")
	public List<BranchCode> create(@RequestBody BranchCode branchCode) {
		codeRepository.save(branchCode);
		
		return codeRepository.findAll();
	}
	
	@PostMapping("/code/{id}")
	public List<BranchCode> update(@PathVariable Long id, @RequestBody BranchCode branchCode) {
		BranchCode code = codeRepository.findOne(id);
		code.setBranchCode(branchCode.getBranchCode());
		code.setName(branchCode.getName());
		code.setAddress(branchCode.getAddress());
		codeRepository.save(code);
		
		return codeRepository.findAll();
	}
	
	
}
