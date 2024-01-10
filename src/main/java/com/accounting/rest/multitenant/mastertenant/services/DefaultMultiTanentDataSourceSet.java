package com.accounting.rest.multitenant.mastertenant.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accounting.rest.multitenant.mastertenant.entity.MasterTenant;
import com.accounting.rest.multitenant.mastertenant.repository.MasterTenantRepository;

@Service
@Transactional("masterTransactionManager")
public class DefaultMultiTanentDataSourceSet {
	@Autowired
	MasterTenantRepository masterTenantRepository;

	public void insertDefaultMasterTenant() {

		List<MasterTenant> Tenants = masterTenantRepository.findByDbName("default_tanent");

		if (Tenants.isEmpty()) {
			MasterTenant defaultTenant = new MasterTenant("default_tanent", "", "default", "default", "default",
					"ACTIVE");
			masterTenantRepository.save(defaultTenant);
		}

	}
}
