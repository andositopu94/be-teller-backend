package com.multipolar.uiteller.backend.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.multipolar.uiteller.backend.model.TransactionLog;

@Repository
public interface TraceNumberRepository extends JpaRepository<TransactionLog, Long>{
	@Query(value = "SELECT trace_sequence.nextval from DUAL", nativeQuery = true)
	BigInteger getTraceNumber();
	
	@Query(value = "SELECT ref_sequence.nextval from DUAL", nativeQuery = true)
	BigInteger getReferenceNumber();
	
}
