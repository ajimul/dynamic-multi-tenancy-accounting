package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accounting.rest.multitenant.tenant.entity.TradingItemsSerialNo;

public interface TradingItemsSerialNoRepo extends JpaRepository<TradingItemsSerialNo, Long> {

}
