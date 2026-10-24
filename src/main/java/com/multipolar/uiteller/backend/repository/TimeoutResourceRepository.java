package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.TimeoutResource;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeoutResourceRepository extends JpaRepository<TimeoutResource, Long> {
    TimeoutResource findFirstByTrxTypeAndCode(String trxType, String code);
    
    List<TimeoutResource> findByTrxTypeAndCode(String trxType, String code);
}
