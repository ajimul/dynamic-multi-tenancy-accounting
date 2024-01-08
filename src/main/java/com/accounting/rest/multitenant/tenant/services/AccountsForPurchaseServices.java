package com.accounting.rest.multitenant.tenant.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accounting.rest.multitenant.dto.TransactionalAccountDTO;
import com.accounting.rest.multitenant.tenant.entity.AccountsForPurchase;
import com.accounting.rest.multitenant.tenant.post.service.PostAccountPurchase;
import com.accounting.rest.multitenant.tenant.repository.AccountsForPurchaseRepo;

@Service
//@Transactional()//Use For Single Database
@Transactional("tenantTransactionManager")//Use For Multitenant

public class AccountsForPurchaseServices {

	@Autowired
	private  AccountsForPurchaseRepo accountsForPurchaseRepo;
	@Autowired
	private  AccountsServices accountsServices;
	@Autowired
	public PostAccountPurchase postAccountPurchase;


//	@PostConstruct
	public void PurchaseAccountList() {
		postAccountPurchase.AccountsPurchaseList();
	}
	public Optional<AccountsForPurchase> getAccounts(String typeName) {
		AccountsForPurchase ac = new AccountsForPurchase();
		ac = accountsForPurchaseRepo.GetAccountIdByAccountName(typeName);
		Optional<AccountsForPurchase> opt = Optional.ofNullable(ac);
		return opt;

	}

	public List<TransactionalAccountDTO> getAccountForPurchase() {
		List<AccountsForPurchase> getAc = new ArrayList<AccountsForPurchase>();
		getAc = accountsForPurchaseRepo.findAll();
		List<TransactionalAccountDTO> newGenericsAccounts = new ArrayList<TransactionalAccountDTO>();
		for (AccountsForPurchase purchaseAccount : getAc) {
			TransactionalAccountDTO genericsAccounts = new TransactionalAccountDTO();
			genericsAccounts = accountsServices.getTransactionalAccount(purchaseAccount.getAccountName());
			newGenericsAccounts.add(genericsAccounts);
		}

		return newGenericsAccounts;
	}

}
