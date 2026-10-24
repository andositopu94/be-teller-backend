package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.InstansiPdam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstansiPdamRepository extends JpaRepository<InstansiPdam, Long> {
	InstansiPdam findByKodeInstansi(String kode);
	InstansiPdam findByNamaInstansi(String namaInstansi);
}
