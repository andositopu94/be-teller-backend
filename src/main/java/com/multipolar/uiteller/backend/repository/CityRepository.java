package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
	City findByCityCode(String s);
	City findByCoreCode(String s);
}
