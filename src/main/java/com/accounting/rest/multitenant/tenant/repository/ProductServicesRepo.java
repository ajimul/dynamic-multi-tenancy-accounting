package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accounting.rest.multitenant.tenant.entity.ProductServices;

public interface ProductServicesRepo extends JpaRepository<ProductServices, Long> {

}
