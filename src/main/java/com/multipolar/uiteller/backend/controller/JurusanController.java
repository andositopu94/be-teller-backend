package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.Jurusan;
import com.multipolar.uiteller.backend.repository.JurusanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class JurusanController {
	
	@Autowired
	private JurusanRepository jurusanRepository;

	@GetMapping("/setup-jurusan")
	public List<Jurusan> getAll() {
		return jurusanRepository.findAll();
	}
	
	@PostMapping("/setup-jurusan")
	public List<Jurusan> create(@RequestBody Jurusan template) {
		jurusanRepository.save(template);
		
		return jurusanRepository.findAll();
	}
	
	@PostMapping("/setup-jurusan/{id}")
	public List<Jurusan> update(@PathVariable Long id, @RequestBody Jurusan template) {
		Jurusan old = jurusanRepository.findOne(id);
		old.setKodeJurusan(template.getKodeJurusan());
		old.setNamaJurusan(template.getNamaJurusan());
		jurusanRepository.save(old);
		
		return jurusanRepository.findAll();
	}
	
	@DeleteMapping("setup-jurusan/{id}")
	public List<Jurusan> delete(@PathVariable Long id) {
		Jurusan template = jurusanRepository.findOne(id);
		jurusanRepository.delete(template);
		
		return jurusanRepository.findAll();
	}
	
	@GetMapping("setup-jurusan/{id}")
	public Jurusan findById(@PathVariable Long id) {
		return jurusanRepository.findOne(id);
	}

	@GetMapping("setup-jurusan-pendidikan/{id}")
	public List<Jurusan> findByPendidikanId(@PathVariable Long id) {
		return jurusanRepository.findByPendidikanId(id);
	}
}
