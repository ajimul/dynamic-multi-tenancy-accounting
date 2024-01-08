package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accounting.rest.multitenant.tenant.entity.ItemsLocationList;

public interface ItemsLocationListRepo extends JpaRepository<ItemsLocationList, Long> {

}
