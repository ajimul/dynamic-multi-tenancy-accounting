package com.accounting.rest.multitenant.tenant.post.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accounting.rest.multitenant.tenant.entity.AccountsForAMC;
import com.accounting.rest.multitenant.tenant.repository.AccountsForAMCRepo;

@Service
//@Transactional() // Use For Single Database
@Transactional("tenantTransactionManager")//Use For Multitenant
public class PostAccountAMC {
	@Autowired
	private  AccountsForAMCRepo accountsForAMCRepo;

	public void AccountAMCList() {
		ArrayList<String> preeAccounts = new ArrayList<>();
		preeAccounts.add("Sundry Debtors");
		preeAccounts.add("Cash Account");
		preeAccounts.add("Bank Account");

		List<AccountsForAMC> acList = new ArrayList<>();

		for (String preeAccount : preeAccounts) {
			Optional<AccountsForAMC> optionalAc = Optional.ofNullable(new AccountsForAMC());
			optionalAc = getAccountsAMC(preeAccount);
			if (!optionalAc.isPresent()) {
				AccountsForAMC ac = new AccountsForAMC();
				ac.setAccountName(preeAccount);
				acList.add(ac);
			}

		}

		if (acList.size() != 0) {
			accountsForAMCRepo.saveAll(acList);
		}

	}

	public Optional<AccountsForAMC> getAccountsAMC(String typeName) {
		AccountsForAMC ac = new AccountsForAMC();
		ac = accountsForAMCRepo.GetAccountIdByAccountName(typeName);
		Optional<AccountsForAMC> opt = Optional.ofNullable(ac);
		return opt;

	}
}
