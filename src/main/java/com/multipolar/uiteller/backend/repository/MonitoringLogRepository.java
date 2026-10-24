package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.MonitoringLog;
import com.multipolar.uiteller.backend.resource.Troubleshoot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MonitoringLogRepository extends JpaRepository<MonitoringLog, Long> {

    List<MonitoringLog> findByTimeoutResourceIdIsNotNullAndTroubleshootAndUserId(Troubleshoot troubleshoot, String userId);

    List<MonitoringLog> findByTroubleshootAndTroubleshootFlagAdmin(Troubleshoot troubleshoot, Troubleshoot troubleshootAdmin);

    @Query("select m from MonitoringLog m where ((m.timeoutResourceId is not null and m.troubleshoot = ?1) or (m.troubleshoot = ?1 and m.troubleshootFlagAdmin != ?2)) and m.userId = ?3")
    List<MonitoringLog> findTimeoutResourceIdAndTroubleshootAndTroubleshootAndTrubleshootFlagAndUser(Troubleshoot troubleshoot, Troubleshoot flag, String userId);

}
