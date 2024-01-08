package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accounting.rest.multitenant.tenant.entity.InventoryGroup;

public interface InventoryGroupRepo extends JpaRepository<InventoryGroup, Long> {

}
