package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.Pendidikan;
import com.multipolar.uiteller.backend.repository.PendidikanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class PendidikanController {
	
	@Autowired
	private PendidikanRepository pendidikanRepository;

	@GetMapping("/setup-pendidikan")
	public List<Pendidikan> getAll() {
		String sortBy = "namaInstansi";
		Sort sort = new Sort(Sort.Direction.ASC, sortBy);
		return pendidikanRepository.findAll(sort);
	}
	
	@PostMapping("/setup-pendidikan")
	public List<Pendidikan> create(@RequestBody Pendidikan template) {
		pendidikanRepository.save(template);
		
		return pendidikanRepository.findAll();
	}
	
	@PostMapping("/setup-pendidikan/{id}")
	public List<Pendidikan> update(@PathVariable Long id, @RequestBody Pendidikan template) {
		Pendidikan old = pendidikanRepository.findOne(id);
		old.setKodeInstansi(template.getKodeInstansi());
		old.setNamaInstansi(template.getNamaInstansi());
		pendidikanRepository.save(old);
		
		return pendidikanRepository.findAll();
	}
	
	@DeleteMapping("setup-pendidikan/{id}")
	public List<Pendidikan> delete(@PathVariable Long id) {
		Pendidikan template = pendidikanRepository.findOne(id);
		pendidikanRepository.delete(template);
		
		return pendidikanRepository.findAll();
	}
	
	@GetMapping("setup-pendidikan/{id}")
	public Pendidikan findById(@PathVariable Long id) {
		return pendidikanRepository.findOne(id);
	}
}
