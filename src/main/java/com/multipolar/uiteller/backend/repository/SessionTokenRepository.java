package com.multipolar.uiteller.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.SessionToken;

public interface SessionTokenRepository extends JpaRepository<SessionToken, Long> {
    List<SessionToken> findByUsername(String username);
}
