package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.ReprintTimeoutResource;
import com.multipolar.uiteller.backend.model.TimeoutResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReprintTimeoutResourceRepository extends JpaRepository<ReprintTimeoutResource, Long> {
    ReprintTimeoutResource findFirstByTrxTypeAndCode(String trxType, String code);
    
    List<ReprintTimeoutResource> findByTrxTypeAndCode(String trxType, String code);
}
