package com.accounting.rest.multitenant.mastertenant.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accounting.rest.multitenant.mastertenant.entity.MasterTenant;
import com.accounting.rest.multitenant.mastertenant.repository.MasterTenantRepository;
import com.accounting.rest.multitenant.tenant.config.SchemaService;

@Service
public class MasterTenantServiceImpl implements MasterTenantService {

	private static final Logger LOG = LoggerFactory.getLogger(MasterTenantServiceImpl.class);

	@Autowired
	MasterTenantRepository masterTenantRepository;
	@Autowired
	private SchemaService schemaService;

	@Override
	public MasterTenant findByClientId(Integer clientId) {
		LOG.info("findByClientId() method call...");
		return masterTenantRepository.findByTenantClientId(clientId);
	}

//	@Autowired
//	private BCryptPasswordEncoder passwordEncoder;

	public String add_Tenant(MasterTenant masterTenant) {
		MasterTenant newMasterTenant = new MasterTenant();
		List<MasterTenant> Tenants = masterTenantRepository.findByDbName(masterTenant.getDbName());

		if (Tenants.isEmpty()) {
			newMasterTenant.setUserName(masterTenant.getUserName());
//			newMasterTenant.setPassword(passwordEncoder.encode(masterTenant.getPassword()));
			newMasterTenant.setPassword(masterTenant.getPassword());
			newMasterTenant.setUrl(masterTenant.getUrl());
			newMasterTenant.setDriverClass(masterTenant.getDriverClass());
			newMasterTenant.setStatus(masterTenant.getStatus());
			newMasterTenant.setDbName(masterTenant.getDbName());
			masterTenantRepository.save(newMasterTenant);
			schemaService.createOrUpdateSchema();
			return "Tanent Create Success! Thank You!";

		} else {
			return "Tanent Already Exist";

		}

	}

}
