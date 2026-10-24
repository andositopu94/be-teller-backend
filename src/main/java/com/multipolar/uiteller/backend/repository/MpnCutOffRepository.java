package com.multipolar.uiteller.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.MpnCutOff;

public interface MpnCutOffRepository extends JpaRepository<MpnCutOff, Long> {
}
