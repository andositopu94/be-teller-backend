package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvinceRepository extends JpaRepository<Province, Long> {
	Province findByProvinceCode(String s);
}
