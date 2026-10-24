package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.ResourceMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourceMessageRepository extends JpaRepository<ResourceMessage, Long> {
    ResourceMessage findByCodeAndMessage(String code, String message);

    @Query(value = "select CODE, MAX(ID) ID, MAX(MESSAGE) MESSAGE, MAX(VALUE) VALUE, MAX(CODE_NAME) CODE_NAME from RESOURCE_MESSAGE GROUP BY code", nativeQuery = true)
    List<ResourceMessage> findTrxType();

    ResourceMessage findFirstByCode(String code);
}
