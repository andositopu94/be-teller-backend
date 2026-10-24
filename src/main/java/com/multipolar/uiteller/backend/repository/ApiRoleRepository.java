package com.multipolar.uiteller.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.ApiRole;

public interface ApiRoleRepository extends JpaRepository<ApiRole, Long> {
    ApiRole findByRoleName(String roleName);
}
