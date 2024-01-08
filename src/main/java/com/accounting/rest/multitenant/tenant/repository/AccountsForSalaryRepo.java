
package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.accounting.rest.multitenant.tenant.entity.AccountsForSalary;

public interface AccountsForSalaryRepo extends JpaRepository<AccountsForSalary, Long> {
	@Query("select u from AccountsForSalary u where u.accountName = :accountName")
	AccountsForSalary GetAccountIdByAccountName(@Param("accountName") String accountName);
}