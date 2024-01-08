package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accounting.rest.multitenant.tenant.entity.ServiceProductSerialNo;

public interface ServiceProductSerialNoRepo extends JpaRepository<ServiceProductSerialNo, Long> {

}
