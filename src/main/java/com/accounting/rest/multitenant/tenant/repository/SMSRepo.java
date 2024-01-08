package com.accounting.rest.multitenant.tenant.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.accounting.rest.multitenant.tenant.entity.SMS;

public interface SMSRepo extends JpaRepository<SMS, Long> {
	@Modifying
	@Transactional
	@Query(value = "TRUNCATE TABLE sms", nativeQuery = true)
	void truncateTable();

}
