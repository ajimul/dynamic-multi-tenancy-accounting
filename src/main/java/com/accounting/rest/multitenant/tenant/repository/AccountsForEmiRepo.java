
package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.accounting.rest.multitenant.tenant.entity.AccountsForEmi;

public interface AccountsForEmiRepo extends JpaRepository<AccountsForEmi, Long> {
	@Query("select u from AccountsForEmi u where u.accountName = :accountName")
	AccountsForEmi GetAccountIdByAccountName(@Param("accountName") String accountName);
}