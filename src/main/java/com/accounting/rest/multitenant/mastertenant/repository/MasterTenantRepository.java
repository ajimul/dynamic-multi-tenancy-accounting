package com.accounting.rest.multitenant.mastertenant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.accounting.rest.multitenant.mastertenant.entity.MasterTenant;

@Repository
public interface MasterTenantRepository extends JpaRepository<MasterTenant, Integer> {
	MasterTenant findByTenantClientId(Integer clientId);

	List<MasterTenant> findByDbName(String dbName);
}
