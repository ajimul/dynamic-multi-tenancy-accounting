package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accounting.rest.multitenant.tenant.entity.PartyDetails;

public interface PartyDetailsRepo extends JpaRepository<PartyDetails, Long> {

}
