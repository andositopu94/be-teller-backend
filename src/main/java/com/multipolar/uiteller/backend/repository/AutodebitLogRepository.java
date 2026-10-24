package com.multipolar.uiteller.backend.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.AutodebitLog;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AutodebitLogRepository extends JpaRepository<AutodebitLog, Long>, JpaSpecificationExecutor<AutodebitLog> {

}
