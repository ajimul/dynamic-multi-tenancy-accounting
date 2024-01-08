package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accounting.rest.multitenant.tenant.entity.LedgerDetails;

public interface LedgerDetailsRepo extends JpaRepository<LedgerDetails, Long> {

}
