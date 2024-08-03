package com.accounting.rest.multitenant.tenant.post.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accounting.rest.multitenant.tenant.entity.AccountsForSales;
import com.accounting.rest.multitenant.tenant.repository.AccountsForSalesRepo;

@Service
//@Transactional() // Use For Single Database
@Transactional("tenantTransactionManager")//Use For Multitenant
public class PostAccountSales {
	@Autowired
	private  AccountsForSalesRepo accountsForSalesRepo;
	public void AccountsSalesList() {
		ArrayList<String> preeAccounts = new ArrayList<>();
		preeAccounts.add("Sundry Debtors");
		preeAccounts.add("Cash Account");
		preeAccounts.add("Bank Account");

		List<AccountsForSales> acList = new ArrayList<>();
		for (String preeAccount : preeAccounts) {
			Optional<AccountsForSales> optionalAc = Optional.ofNullable(new AccountsForSales());
			optionalAc = getAccountsSales(preeAccount);
			if (!optionalAc.isPresent()) {
				AccountsForSales ac = new AccountsForSales();
				ac.setAccountName(preeAccount);
				acList.add(ac);
			}

		}

		if (acList.size() != 0) {
			accountsForSalesRepo.saveAll(acList);
		}

	}

	public Optional<AccountsForSales> getAccountsSales(String typeName) {
		AccountsForSales ac = new AccountsForSales();
		ac = accountsForSalesRepo.GetAccountIdByAccountName(typeName);
		Optional<AccountsForSales> opt = Optional.ofNullable(ac);
		return opt;

	}
}
