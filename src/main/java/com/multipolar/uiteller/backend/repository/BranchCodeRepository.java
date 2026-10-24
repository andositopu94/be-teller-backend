package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.BranchCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchCodeRepository extends JpaRepository<BranchCode, Long> {
	BranchCode findByBranchCode(String s);
}
