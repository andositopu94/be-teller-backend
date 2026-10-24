package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.SuspectLogAction;
import com.multipolar.uiteller.backend.model.SuspectLogMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SuspectLogActionRepository extends JpaRepository<SuspectLogAction, Long> {

    @Query(value = "select ACTION " +
            "  from SUSPECT_LOG_ACTION " +
            " where id = ( select max(id) from SUSPECT_LOG_ACTION WHERE SUSPECT_LOG_MONITORING_ID = :suspectId)",
            nativeQuery = true)
    String findLastActionBySuspectId(@Param("suspectId") Long suspectId);

}
