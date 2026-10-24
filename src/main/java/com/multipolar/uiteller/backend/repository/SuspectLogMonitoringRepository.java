package com.multipolar.uiteller.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.multipolar.uiteller.backend.model.SuspectLogMonitoring;

public interface SuspectLogMonitoringRepository extends JpaRepository<SuspectLogMonitoring, Long> {
    SuspectLogMonitoring findByCommonTransactionLog_Id(Long id);
    
    @Query(value="SELECT * FROM SUSPECT_LOG_MONITORING slm FULL JOIN COMMON_TRANSACTION_LOG ctl ON slm.COMMON_TRANSACTION_LOG_ID = ctl.ID WHERE ctl.USERID= :userId AND slm.STATUS IS NOT NULL", nativeQuery=true)
    List<SuspectLogMonitoring> findByUserId(@Param("userId")String userId);

    @Query(value="SELECT * FROM SUSPECT_LOG_MONITORING slm FULL JOIN COMMON_TRANSACTION_LOG ctl ON slm.COMMON_TRANSACTION_LOG_ID = ctl.ID WHERE slm.STATUS IS NOT NULL AND TRUNC( ctl.TRX_DATE ) > TRUNC( SYSDATE ) - INTERVAL '1' DAY AND TRUNC( ctl.TRX_DATE ) < TRUNC( SYSDATE ) + INTERVAL '1' DAY ORDER BY slm.ID desc", nativeQuery=true)
    List<SuspectLogMonitoring> findByAll();

    @Query(value="SELECT * FROM SUSPECT_LOG_MONITORING slm FULL JOIN COMMON_TRANSACTION_LOG ctl ON slm.COMMON_TRANSACTION_LOG_ID = ctl.ID WHERE ctl.USERID= :userId AND slm.STATUS IS NOT NULL AND TRUNC( ctl.TRX_DATE ) > TRUNC( SYSDATE ) - INTERVAL '1' DAY AND TRUNC( ctl.TRX_DATE ) < TRUNC( SYSDATE ) + INTERVAL '1' DAY AND CAST(slm.STATUS as INT) >= 0 ORDER BY slm.ID desc", nativeQuery=true)
    List<SuspectLogMonitoring> findByUserIdToday(@Param("userId")String userId);
    
    @Query(value="SELECT * FROM SUSPECT_LOG_MONITORING slm FULL JOIN COMMON_TRANSACTION_LOG ctl ON slm.COMMON_TRANSACTION_LOG_ID = ctl.ID "
    		+ "WHERE TRUNC( ctl.TRX_DATE ) = TRUNC( SYSDATE ) AND slm.STATUS = '03' ORDER BY slm.ID desc", nativeQuery=true)
    List<SuspectLogMonitoring> findByStatusAndToday();
}
