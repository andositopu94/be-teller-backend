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

import com.multipolar.uiteller.backend.model.SysParam;
import com.multipolar.uiteller.backend.repository.SysParamRepository;

@RestController
@RequestMapping("/admin")
public class SysParamController {
	@Autowired
	private SysParamRepository sysParamRepository;

	@GetMapping("/sysParams")
	public List<SysParam> getAll() {
		return sysParamRepository.findAll();
	}
	
	@PostMapping("/sysParam")
	public List<SysParam> create(@RequestBody SysParam sysParam) {
		sysParamRepository.save(sysParam);
		
		return sysParamRepository.findAll();
	}
	
	@PostMapping("/sysParam/{id}")
	public List<SysParam> update(@PathVariable Long id, @RequestBody SysParam sysParam) {
		SysParam old = sysParamRepository.findOne(id);		
		sysParamRepository.save(old);
		
		return sysParamRepository.findAll();
	}
	
	@DeleteMapping("sysParam/{id}")
	public List<SysParam> delete(@PathVariable Long id) {
		SysParam sysParam = sysParamRepository.findOne(id);
		sysParamRepository.delete(sysParam);
		
		return sysParamRepository.findAll();
	}
	
	@GetMapping("sysParam/{id}")
	public SysParam findById(@PathVariable Long id) {
		return sysParamRepository.findOne(id);
	}
}
