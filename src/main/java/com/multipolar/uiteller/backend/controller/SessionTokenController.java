package com.multipolar.uiteller.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multipolar.uiteller.backend.model.SessionToken;
import com.multipolar.uiteller.backend.repository.SessionTokenRepository;

@RestController
@RequestMapping("/admin")
public class SessionTokenController {
	@Autowired
	private SessionTokenRepository sessionRepository;

	@GetMapping("/sessions")
	public List<SessionToken> getAll() {
		return sessionRepository.findAll();
	}
	
	@DeleteMapping("sessions/{id}")
	public List<SessionToken> delete(@PathVariable Long id) {
		SessionToken token = sessionRepository.findOne(id);
		sessionRepository.delete(token);
		
		return sessionRepository.findAll();
	}
}
