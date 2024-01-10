package com.accounting.rest.multitenant.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accounting.rest.multitenant.mastertenant.entity.MasterTenant;
import com.accounting.rest.multitenant.mastertenant.services.MasterTenantServiceImpl;
import com.accounting.rest.multitenant.tenant.entity.Users;
import com.accounting.rest.multitenant.tenant.repository.UsersRepo;

@RestController
@RequestMapping("/api/tenant")
public class TenantInitializerController {
	@Autowired
	MasterTenantServiceImpl masterTenantServiceImpl;
	@Autowired
	UsersRepo userRepository;

	@PostMapping("/add")
	public ResponseEntity<String> add_Tenant(@RequestBody MasterTenant masterTenant) {

		return new ResponseEntity<>(masterTenantServiceImpl.add_Tenant(masterTenant), HttpStatus.CREATED);

	}

	@PostMapping("/user")
	public ResponseEntity<Users> add_User(@RequestBody Users user) {
		return new ResponseEntity<>(userRepository.save(user), HttpStatus.CREATED);

	}

}
