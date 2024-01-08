package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accounting.rest.multitenant.tenant.entity.Bom;

public interface BomRepo extends JpaRepository<Bom, Long> {

}
