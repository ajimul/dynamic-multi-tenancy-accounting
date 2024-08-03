package com.accounting.rest.multitenant.tenant.post.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accounting.rest.multitenant.tenant.entity.AccountsForSalary;
import com.accounting.rest.multitenant.tenant.repository.AccountsForSalaryRepo;

@Service
//@Transactional() // Use For Single Database
@Transactional("tenantTransactionManager")//Use For Multitenant
public class PostAccountSalary {
	@Autowired
	private  AccountsForSalaryRepo accountsForSalaryRepo;
	public void AccountSalaryList() {
		ArrayList<String> preeAccounts = new ArrayList<>();
		preeAccounts.add("Cash Account");
		preeAccounts.add("Bank Account");

		List<AccountsForSalary> acList = new ArrayList<>();
		for (String preeAccount : preeAccounts) {
			Optional<AccountsForSalary> optionalAc = Optional.ofNullable(new AccountsForSalary());
			optionalAc = getAccountsSalary(preeAccount);
			if (!optionalAc.isPresent()) {
				AccountsForSalary ac = new AccountsForSalary();
				ac.setAccountName(preeAccount);
				acList.add(ac);
			}

		}

		if (acList.size() != 0) {
			accountsForSalaryRepo.saveAll(acList);
		}

	}

	public Optional<AccountsForSalary> getAccountsSalary(String typeName) {
		AccountsForSalary ac = new AccountsForSalary();
		ac = accountsForSalaryRepo.GetAccountIdByAccountName(typeName);
		Optional<AccountsForSalary> opt = Optional.ofNullable(ac);
		return opt;

	}
}
