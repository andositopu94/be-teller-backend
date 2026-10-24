package com.multipolar.uiteller.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.ReverseHistory;

public interface ReverseHistoryRepository extends JpaRepository<ReverseHistory, Long> {
}
