package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.Job;
import com.multipolar.uiteller.backend.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class JobController {
	
	@Autowired
	private JobRepository jobRepository;

	@GetMapping("/setup-job")
	public List<Job> getAll() {
		return jobRepository.findAll();
	}

}
