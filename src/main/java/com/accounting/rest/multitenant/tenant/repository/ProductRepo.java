package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accounting.rest.multitenant.tenant.entity.Product;

public interface ProductRepo extends JpaRepository<Product, Long> {

}
