package com.multipolar.uiteller.backend.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multipolar.uiteller.backend.model.ApiComponent;
import com.multipolar.uiteller.backend.model.ApiRole;
import com.multipolar.uiteller.backend.model.Role;
import com.multipolar.uiteller.backend.repository.ApiComponentRepository;
import com.multipolar.uiteller.backend.repository.ApiRoleRepository;
import com.multipolar.uiteller.backend.repository.RoleRepository;

@RestController
@RequestMapping("/admin")
public class RoleController {
	@Autowired
	private ApiRoleRepository roleRepository;
	
	@Autowired
	private RoleRepository repository;
	
	
	@Autowired
	private ApiComponentRepository componentRepository;
	
	@GetMapping("/roles")
	public List<ApiRole> getRoles() {
		return roleRepository.findAll();
	}
	
	@GetMapping("/role")
	public List<Role> getAllRoles() {
		return repository.findAll();
	}
	
	@PostMapping("/role")
	public List<Role> create(@RequestBody Role role) {
		role.setMenuId("100");
		repository.save(role);
		
		return repository.findAll();
	}
	
	@DeleteMapping("role/{id}")
	public List<Role> delete(@PathVariable Long id) {
		Role role = repository.findOne(id);
		repository.delete(role);
		
		return repository.findAll();
	}
	
	@PostMapping("/role/{id}")
	public List<Role> update(@PathVariable Long id, @RequestBody Role role) {
		Role old = repository.findOne(id);
		old.setId(role.getId());
		old.setName(role.getName());
		old.setMenuId(role.getMenuId());
		repository.save(old);
		
		return repository.findAll();
	}
	
	@GetMapping("role/{id}")
	public Role findById(@PathVariable Long id) {
		return repository.findOne(id);
	}
	
	@GetMapping("/components")
	public List<ApiComponent> getComponents() {
		List<ApiComponent> components = componentRepository.findAll();
		for (ApiComponent component : components) {
			Set<ApiRole> roles = this.getRolesFromComponent(component);
			component.setListOfRole(roles);
		}
		return components;
	}
	
	@GetMapping("/component/roles/{apiId}")
	public List<ApiRole> getRoleByComponent(@PathVariable Long apiId) {
		ApiComponent component = componentRepository.findOne(apiId);
		if (component == null)
			throw new IllegalArgumentException("Component tidak dapat ditemukan");
		
		return new ArrayList<>(component.getRoles());
	}
	
	@GetMapping("role/components/{roleId}")
	public List<ApiComponent> getComponentByRole(Long roleId) {
		ApiRole role = roleRepository.findOne(roleId);
		if (role == null)
			throw new IllegalArgumentException("Role Tidak dapat ditemukan");
		
		return new ArrayList<>(role.getApis());
	}
	
	/*
	@PostMapping("/role/{roleId}")
	public List<ApiRole> addComponentToRole(@PathVariable Long roleId, @RequestBody List<ApiComponent> components) {
		ApiRole role = roleRepository.findOne(roleId);
		if (role == null)
			throw new IllegalArgumentException("role tidak dapat ditemukan");
		
		role.getApis().addAll(components);	
		roleRepository.save(role);
		
		return roleRepository.findAll();
	}
	*/
	
	@PostMapping("/component/{componentId}")
	public List<ApiComponent> addRoleToComponent(@PathVariable Long componentId, @RequestBody List<ApiRole> roles) {
		ApiComponent component = componentRepository.findOne(componentId);
		if (component == null)
			throw new IllegalArgumentException("component tidak dapat ditemukan");
		
		component.getRoles().addAll(roles);
		componentRepository.save(component);
	
		return componentRepository.findAll();
	}
	
	/*
	@DeleteMapping("/role/{roleId}")
	public List<ApiRole> removeComponentFromRole(Long roleId, List<ApiComponent> components) {
		ApiRole role = roleRepository.findOne(roleId);
		if (role == null)
			throw new IllegalArgumentException("role tidak dapat ditemukan");
		
		role.getApis().removeAll(components);	
		roleRepository.save(role);
		
		return roleRepository.findAll();
	}
	*/
	
	@DeleteMapping("/component/{componentId}")
	public List<ApiComponent> removeRoleToComponent(@PathVariable Long componentId, @RequestBody List<ApiRole> roles) {
		ApiComponent component = componentRepository.findOne(componentId);
		if (component == null)
			throw new IllegalArgumentException("component tidak dapat ditemukan");
		
		component.getRoles().removeAll(roles);
		componentRepository.save(component);
	
		return componentRepository.findAll();
	}
	
	// this method is used to remove cyclic dependency
	private Set<ApiRole> getRolesFromComponent(ApiComponent component) {
		Set<ApiRole> roles = component.getRoles();
		for (ApiRole role : roles) {
			role.setApis(new HashSet<ApiComponent>());
		}
		
		return roles;
	}
}
