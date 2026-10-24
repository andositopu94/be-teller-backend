package com.multipolar.uiteller.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.AdminCharge;

public interface AdminChargeRepository extends JpaRepository<AdminCharge, Long> {
	AdminCharge findByTransaction(String trx);
}
