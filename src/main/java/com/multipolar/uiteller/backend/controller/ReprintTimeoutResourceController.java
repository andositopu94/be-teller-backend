package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.ReprintTimeoutResource;
import com.multipolar.uiteller.backend.repository.ReprintTimeoutResourceRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/adminReprint")
public class ReprintTimeoutResourceController {

	@Autowired
	private ReprintTimeoutResourceRepository reprintTimeoutResourceRepository;

	@GetMapping("/reprintTimeoutResources")
	public List<ReprintTimeoutResource> getAll() {
		return reprintTimeoutResourceRepository.findAll();
	}
	
	@PostMapping("/reprintTimeoutResource")
	public List<ReprintTimeoutResource> create(@RequestBody ReprintTimeoutResource reprintTimeoutResource) throws InterruptedException, BusinessException, IOException{
		try{
			reprintTimeoutResourceRepository.save(reprintTimeoutResource);
		}
		catch (DataIntegrityViolationException e){
			e.printStackTrace();
	        throw new BusinessException("Kode respon untuk jenis transaksi ini sudah terdaftar");
		}
		return reprintTimeoutResourceRepository.findAll();
	}
	
	@PostMapping("reprintTimeoutResource/{id}")
	public List<ReprintTimeoutResource> update(@PathVariable Long id, @RequestBody ReprintTimeoutResource reprintTimeoutResource) throws InterruptedException, BusinessException, IOException{
		ReprintTimeoutResource data = reprintTimeoutResourceRepository.findOne(id);
		try{
		reprintTimeoutResourceRepository.save(data);
		}
		catch (DataIntegrityViolationException e){
        throw new BusinessException("Kode respon untuk jenis transaksi ini sudah terdaftar");
		}
		return reprintTimeoutResourceRepository.findAll();
	}
	
	@DeleteMapping("reprintTimeoutResource/{id}")
	public List<ReprintTimeoutResource> delete(@PathVariable Long id) {
		ReprintTimeoutResource reprintTimeoutResource = reprintTimeoutResourceRepository.findOne(id);
		reprintTimeoutResourceRepository.delete(reprintTimeoutResource);
		
		return reprintTimeoutResourceRepository.findAll();
	}
	
	@GetMapping("reprintTimeoutResource/{id}")
	public ReprintTimeoutResource findById(@PathVariable Long id) {
		return reprintTimeoutResourceRepository.findOne(id);
	}
}

