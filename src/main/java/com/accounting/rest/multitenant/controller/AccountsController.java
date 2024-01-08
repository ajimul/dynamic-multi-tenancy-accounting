package com.accounting.rest.multitenant.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accounting.rest.multitenant.dto.EmployeeDetailsDTO;
import com.accounting.rest.multitenant.dto.PartyDetailsDTO;
import com.accounting.rest.multitenant.dto.PartyModel;
import com.accounting.rest.multitenant.dto.TransactionalAccountDTO;
import com.accounting.rest.multitenant.tenant.services.AccountsForAMCServices;
import com.accounting.rest.multitenant.tenant.services.AccountsForEmiServices;
import com.accounting.rest.multitenant.tenant.services.AccountsForPurchaseServices;
import com.accounting.rest.multitenant.tenant.services.AccountsForSalaryServices;
import com.accounting.rest.multitenant.tenant.services.AccountsForSalesServices;
import com.accounting.rest.multitenant.tenant.services.AccountsServices;

@RestController
//@CrossOrigin
@RequestMapping("api/accounts")
public class AccountsController {
	@Autowired
	private AccountsServices accountsServices;
	@Autowired
	private AccountsForSalesServices accountsForSalesServices;
	@Autowired
	private AccountsForSalaryServices accountsForSalaryServices;
	@Autowired
	private AccountsForEmiServices accountsForEmiServices;
	@Autowired
	private AccountsForPurchaseServices accountsForPurchaseServices;
	@Autowired
	private AccountsForAMCServices accountsForAMCServices;

// get employee
	@GetMapping("/emp")
	public ResponseEntity<List<EmployeeDetailsDTO>> getEmployee() {
		return new ResponseEntity<>(accountsServices.getEmployeeDetailsList(), HttpStatus.OK);
	}

	// Add Employee
	@PostMapping("/emp/add")
	public ResponseEntity<?> addEmployee(@RequestBody EmployeeDetailsDTO accounts) {
		accountsServices.addEmployee(accounts);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	// Update Employee
	@PutMapping("/emp/update")
	public ResponseEntity<?> updateEmployee(@RequestBody EmployeeDetailsDTO employee) {
		accountsServices.updateEmployee(employee);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	// Get Party From Response Join Table (Accounts+PartyDetails))
	@GetMapping("/party")
	public ResponseEntity<List<PartyDetailsDTO>> getParty() {
		return new ResponseEntity<>(accountsServices.getPartyDetailsList(), HttpStatus.OK);
	}

	// Add Party
	@PostMapping("/party/add")
	public ResponseEntity<?> addParty(@RequestBody PartyModel party) {
		accountsServices.addParty(party);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	// Update Party
	@PutMapping("/party/update")
	public ResponseEntity<?> updateParty(@RequestBody PartyDetailsDTO partyDetailsDTO) {
		accountsServices.updateParty(partyDetailsDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/sales")
	public ResponseEntity<List<TransactionalAccountDTO>> getAccountForSales() {
		return new ResponseEntity<>(accountsForSalesServices.getAccountForSales(), HttpStatus.OK);

	}

	@GetMapping("/salary")
	public ResponseEntity<List<TransactionalAccountDTO>> getAccountForSalary() {
		return new ResponseEntity<>(accountsForSalaryServices.getAccountForSalary(), HttpStatus.OK);

	}

	@GetMapping("/emi")
	public ResponseEntity<List<TransactionalAccountDTO>> getAccountForEmi() {
		return new ResponseEntity<>(accountsForEmiServices.getAccountForEmi(), HttpStatus.OK);

	}

	@GetMapping("/purchase")
	public ResponseEntity<List<TransactionalAccountDTO>> getAccountForPurchase() {
		return new ResponseEntity<>(accountsForPurchaseServices.getAccountForPurchase(), HttpStatus.OK);

	}

	@GetMapping("/amc")
	public ResponseEntity<List<TransactionalAccountDTO>> getAccountForAMC() {
		return new ResponseEntity<>(accountsForAMCServices.getAccountForAMC(), HttpStatus.OK);

	}

}
