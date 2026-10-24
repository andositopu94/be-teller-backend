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

import com.multipolar.uiteller.backend.model.TimeoutResourceMessage;
import com.multipolar.uiteller.backend.repository.TimeoutResourceMessageRepository;

@RestController
@RequestMapping("/admin")
public class TimeoutResourceMessageController {
	@Autowired
	private TimeoutResourceMessageRepository timeoutResourceMessageRepository;

	@GetMapping("/timeoutResourceMessages")
	public List<TimeoutResourceMessage> getAll() {
		return timeoutResourceMessageRepository.findAll();
	}
	
	@PostMapping("/timeoutResourceMessage")
	public List<TimeoutResourceMessage> create(@RequestBody TimeoutResourceMessage timeoutResourceMessage) {
		timeoutResourceMessageRepository.save(timeoutResourceMessage);
		
		return timeoutResourceMessageRepository.findAll();
	}
	
	@PostMapping("/timeoutResourceMessage/{id}")
	public List<TimeoutResourceMessage> update(@PathVariable Long id, @RequestBody TimeoutResourceMessage timeoutResourceMessage) {
		TimeoutResourceMessage old = timeoutResourceMessageRepository.findOne(id);		
		timeoutResourceMessageRepository.save(old);
		
		return timeoutResourceMessageRepository.findAll();
	}
	
	@DeleteMapping("timeoutResourceMessage/{id}")
	public List<TimeoutResourceMessage> delete(@PathVariable Long id) {
		TimeoutResourceMessage timeoutResourceMessage = timeoutResourceMessageRepository.findOne(id);
		timeoutResourceMessageRepository.delete(timeoutResourceMessage);
		
		return timeoutResourceMessageRepository.findAll();
	}
	
	@GetMapping("timeoutResourceMessage/{id}")
	public TimeoutResourceMessage findById(@PathVariable Long id) {
		return timeoutResourceMessageRepository.findOne(id);
	}
}
