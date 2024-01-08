package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accounting.rest.multitenant.tenant.entity.ExtraCost;

public interface ExtraCostRepo extends JpaRepository<ExtraCost, Long> {

}
