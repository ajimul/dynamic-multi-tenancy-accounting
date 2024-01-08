package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accounting.rest.multitenant.tenant.entity.Bomlist;

public interface BomlistRepo extends JpaRepository<Bomlist, Long> {

}
