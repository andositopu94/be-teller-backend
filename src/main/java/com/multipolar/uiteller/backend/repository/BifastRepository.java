package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.BifastBank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BifastRepository  extends JpaRepository<BifastBank, Long> {
    BifastBank findByBankBIC(String s);

    List<BifastBank> findByActive(String s);
}
