package com.accounting.rest.multitenant.tenant.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accounting.rest.multitenant.dto.TransactionalAccountDTO;
import com.accounting.rest.multitenant.tenant.entity.AccountsForAMC;
import com.accounting.rest.multitenant.tenant.post.service.PostAccountAMC;
import com.accounting.rest.multitenant.tenant.repository.AccountsForAMCRepo;

@Service
//@Transactional()//Use For Single Database
@Transactional("tenantTransactionManager")//Use For Multitenant

public class AccountsForAMCServices {
	@Autowired
	private AccountsForAMCRepo accountsForAMCRepo;
	@Autowired
	private AccountsServices accountsServices;
	@Autowired
	private	PostAccountAMC   postAccountAMC;


//	@PostConstruct
	public void AMCAccountList() {
		postAccountAMC.AccountAMCList();
	}

//	// Find Accounts By Name
	public Optional<AccountsForAMC> getAccounts(String typeName) {
		AccountsForAMC ac = new AccountsForAMC();
		ac = accountsForAMCRepo.GetAccountIdByAccountName(typeName);
		Optional<AccountsForAMC> opt = Optional.ofNullable(ac);
		return opt;

	}

	public List<TransactionalAccountDTO> getAccountForAMC() {
		List<AccountsForAMC> getAc = new ArrayList<>();
		getAc = accountsForAMCRepo.findAll();
		List<TransactionalAccountDTO> newGenericsAccounts = new ArrayList<>();
		for (AccountsForAMC amcAccount : getAc) {
			TransactionalAccountDTO genericsAccounts = new TransactionalAccountDTO();
			genericsAccounts = accountsServices.getTransactionalAccount(amcAccount.getAccountName());
			newGenericsAccounts.add(genericsAccounts);
		}

		return newGenericsAccounts;
	}

}
