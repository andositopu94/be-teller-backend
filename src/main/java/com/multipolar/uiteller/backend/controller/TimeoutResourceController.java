package com.multipolar.uiteller.backend.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multipolar.uiteller.backend.model.TimeoutResource;
import com.multipolar.uiteller.backend.repository.TimeoutResourceRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;

@RestController
@RequestMapping("/admin")
public class TimeoutResourceController {
	@Autowired
	private TimeoutResourceRepository timeoutResourceRepository;

	@GetMapping("/timeoutResources")
	public List<TimeoutResource> getAll() {
		return timeoutResourceRepository.findAll();
	}
	
	@PostMapping("/timeoutResource")
	public List<TimeoutResource> create(@RequestBody TimeoutResource timeoutResource) throws InterruptedException, BusinessException, IOException{
		try{
		timeoutResourceRepository.save(timeoutResource);
		}
		catch (DataIntegrityViolationException e){
	        throw new BusinessException("Kode respon untuk jenis transaksi ini sudah terdaftar");
		}
		return timeoutResourceRepository.findAll();
	}
	
	@PostMapping("/timeoutResource/{id}")
	public List<TimeoutResource> update(@PathVariable Long id, @RequestBody TimeoutResource timeoutResource) throws InterruptedException, BusinessException, IOException{
		TimeoutResource old = timeoutResourceRepository.findOne(id);	
		try{
		timeoutResourceRepository.save(old);
		}
		catch (DataIntegrityViolationException e){
        throw new BusinessException("Kode respon untuk jenis transaksi ini sudah terdaftar");
		}
		return timeoutResourceRepository.findAll();
	}
	
	@DeleteMapping("timeoutResource/{id}")
	public List<TimeoutResource> delete(@PathVariable Long id) {
		TimeoutResource timeoutResource = timeoutResourceRepository.findOne(id);
		timeoutResourceRepository.delete(timeoutResource);
		
		return timeoutResourceRepository.findAll();
	}
	
	@GetMapping("timeoutResource/{id}")
	public TimeoutResource findById(@PathVariable Long id) {
		return timeoutResourceRepository.findOne(id);
	}
}

