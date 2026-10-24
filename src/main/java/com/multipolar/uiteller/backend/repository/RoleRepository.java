package com.multipolar.uiteller.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multipolar.uiteller.backend.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	List<Role> findByRoleIdIn(List<String> id);
}
