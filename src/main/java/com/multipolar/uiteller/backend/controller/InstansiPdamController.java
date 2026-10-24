package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.InstansiPdam;
import com.multipolar.uiteller.backend.model.Pendidikan;
import com.multipolar.uiteller.backend.repository.InstansiPdamRepository;
import com.multipolar.uiteller.backend.repository.PendidikanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class InstansiPdamController {
	
	@Autowired
	private InstansiPdamRepository instansiPdamRepository;

	@GetMapping("/setup-instansi-pdam")
	public List<InstansiPdam> getAll() {
		List<InstansiPdam> instansiPdams = instansiPdamRepository.findAll();
		List<InstansiPdam> filteredInstansiPdams = new ArrayList<InstansiPdam>();
		for (InstansiPdam instansi : instansiPdams) {
			if (instansi.getKodeInstansi() != null && instansi.getNamaInstansi()!=null) {
				filteredInstansiPdams.add(instansi);
			}
		}
		return filteredInstansiPdams;
	}
	
	@PostMapping("/setup-instansi-pdam")
	public List<InstansiPdam> create(@RequestBody InstansiPdam template) {
		instansiPdamRepository.save(template);
		
		return instansiPdamRepository.findAll();
	}
	
	@PostMapping("/setup-instansi-pdam/{id}")
	public List<InstansiPdam> update(@PathVariable Long id, @RequestBody InstansiPdam template) {
		InstansiPdam old = instansiPdamRepository.findOne(id);
		old.setKodeInstansi(template.getKodeInstansi());
		old.setNamaInstansi(template.getNamaInstansi());
		instansiPdamRepository.save(old);
		
		return instansiPdamRepository.findAll();
	}
	
	@DeleteMapping("setup-instansi-pdam/{id}")
	public List<InstansiPdam> delete(@PathVariable Long id) {
		InstansiPdam template = instansiPdamRepository.findOne(id);
		instansiPdamRepository.delete(template);
		
		return instansiPdamRepository.findAll();
	}
	
	@GetMapping("setup-instansi-pdam/{id}")
	public InstansiPdam findById(@PathVariable Long id) {
		return instansiPdamRepository.findOne(id);
	}
}
