package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.Jurusan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JurusanRepository extends JpaRepository<Jurusan, Long> {

    List<Jurusan> findByPendidikanId(Long id);
}
