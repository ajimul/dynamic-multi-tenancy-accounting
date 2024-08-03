package com.accounting.rest.multitenant.dto;

import java.time.LocalDate;
import java.util.List;

import com.accounting.rest.multitenant.tenant.entity.Bom;
import com.accounting.rest.multitenant.tenant.entity.BookDetails;
import com.accounting.rest.multitenant.tenant.entity.InventoryJournal;

public class BomModel {

	private Long userId;
	private Long accountId;
	private String partyAcName;
	private LocalDate transectionDate;
	private String naration;
	private TransectionalAccountsModel transectionalAccounts;
	private List<BookDetails> bookDetails;
	private List<InventoryJournal> inventoryJournal;
	private Bom bom;

	public BomModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BomModel(Long userId, Long accountId, String partyAcName, LocalDate transectionDate, String naration,
			TransectionalAccountsModel transectionalAccounts, List<BookDetails> bookDetails,
			List<InventoryJournal> inventoryJournal, Bom bom) {
		super();
		this.userId = userId;
		this.accountId = accountId;
		this.partyAcName = partyAcName;
		this.transectionDate = transectionDate;
		this.naration = naration;
		this.transectionalAccounts = transectionalAccounts;
		this.bookDetails = bookDetails;
		this.inventoryJournal = inventoryJournal;
		this.bom = bom;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getAccountId() {
		return accountId;
	}

	public String getPartyAcName() {
		return partyAcName;
	}

	public LocalDate getTransectionDate() {
		return transectionDate;
	}

	public String getNaration() {
		return naration;
	}

	public TransectionalAccountsModel getTransectionalAccounts() {
		return transectionalAccounts;
	}

	public List<BookDetails> getBookDetails() {
		return bookDetails;
	}

	public List<InventoryJournal> getInventoryJournal() {
		return inventoryJournal;
	}

	public Bom getBom() {
		return bom;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public void setPartyAcName(String partyAcName) {
		this.partyAcName = partyAcName;
	}

	public void setTransectionDate(LocalDate transectionDate) {
		this.transectionDate = transectionDate;
	}

	public void setNaration(String naration) {
		this.naration = naration;
	}

	public void setTransectionalAccounts(TransectionalAccountsModel transectionalAccounts) {
		this.transectionalAccounts = transectionalAccounts;
	}

	public void setBookDetails(List<BookDetails> bookDetails) {
		this.bookDetails = bookDetails;
	}

	public void setInventoryJournal(List<InventoryJournal> inventoryJournal) {
		this.inventoryJournal = inventoryJournal;
	}

	public void setBom(Bom bom) {
		this.bom = bom;
	}

}
