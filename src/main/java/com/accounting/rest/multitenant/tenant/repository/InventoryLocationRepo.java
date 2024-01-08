package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accounting.rest.multitenant.tenant.entity.InventoryLocation;

public interface InventoryLocationRepo extends JpaRepository<InventoryLocation, Long> {

}
