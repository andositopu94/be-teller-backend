package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.InstansiPdam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PDAMRepository extends JpaRepository<InstansiPdam, Long> {
    @Query(value="SELECT NAMA_INSTANSI FROM INSTANSI_PDAM",
            nativeQuery=true)
    String[] fetchPDAMName();
}
