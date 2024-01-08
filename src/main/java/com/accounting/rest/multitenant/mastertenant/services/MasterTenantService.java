package com.accounting.rest.multitenant.mastertenant.services;

import com.accounting.rest.multitenant.mastertenant.entity.MasterTenant;

/**
 * @author Md. Amran Hossain
 */
public interface MasterTenantService {
	MasterTenant findByClientId(Integer clientId);

}
