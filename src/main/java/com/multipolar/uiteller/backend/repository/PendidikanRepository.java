package com.multipolar.uiteller.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.Pendidikan;

public interface PendidikanRepository extends JpaRepository<Pendidikan, Long> {
	Pendidikan findByKodeInstansi(String kode);
}
