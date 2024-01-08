package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accounting.rest.multitenant.tenant.entity.ItemsGroupList;

public interface ItemsGroupListRepo extends JpaRepository<ItemsGroupList, Long> {

}
