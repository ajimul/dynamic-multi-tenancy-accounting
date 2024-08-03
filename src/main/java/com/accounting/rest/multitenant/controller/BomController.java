package com.accounting.rest.multitenant.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accounting.rest.multitenant.dto.BomModel;
import com.accounting.rest.multitenant.tenant.entity.BookDetails;
import com.accounting.rest.multitenant.tenant.entity.BookInfo;
import com.accounting.rest.multitenant.tenant.entity.Books;
import com.accounting.rest.multitenant.tenant.entity.FolioNumber;
import com.accounting.rest.multitenant.tenant.entity.InventoryItems;
import com.accounting.rest.multitenant.tenant.entity.Ledger;
import com.accounting.rest.multitenant.tenant.entity.LedgerDetails;
import com.accounting.rest.multitenant.tenant.services.AccountsServices;
import com.accounting.rest.multitenant.tenant.services.AccountsTypeServices;
import com.accounting.rest.multitenant.tenant.services.BomService;
import com.accounting.rest.multitenant.tenant.services.BookTypeServices;
import com.accounting.rest.multitenant.tenant.services.FolioNumberServices;
import com.accounting.rest.multitenant.tenant.services.InventoryItemsService;

@RestController
//@Transactional()//Use For Single Database
@Transactional("tenantTransactionManager") // Use For Multitenant
//@CrossOrigin(origins = "https://spotsolution.store")
@RequestMapping("api/sales/bom")
@Scope("prototype")
public class BomController {

	private final FolioNumberServices folioNumberServices;
	private final AccountsTypeServices accountsTypeServices;
	private final AccountsServices accountsServices;
	private final BookTypeServices bookTypeServices;
	private final InventoryItemsService inventoryItemsService;
	private final BomService bomService;

	@Autowired
	public BomController(FolioNumberServices folioNumberServices, AccountsTypeServices accountsTypeServices,
			AccountsServices accountsServices, BookTypeServices bookTypeServices,
			InventoryItemsService inventoryItemsService, BomService bomService) {
		super();
		this.folioNumberServices = folioNumberServices;
		this.accountsTypeServices = accountsTypeServices;
		this.accountsServices = accountsServices;
		this.bookTypeServices = bookTypeServices;
		this.inventoryItemsService = inventoryItemsService;
		this.bomService = bomService;
	}

	Double ocgst = 0d;
	Double osgst = 0d;
	Double igst = 0d;
	Double totalCreditAmount = 0d;

	@PostMapping("/add")
	public ResponseEntity<String> addBooks(@RequestBody BomModel books) {

		/**
		 * GST Separation
		 */
		for (BookDetails bookDetails : books.getBookDetails()) {
			// The Line Of Code Used For The Sum Of The OUTPUT CGST Amount For All sold
			// Goods
			ocgst += bookDetails.getBookDetailsCgstAmount();

			// The Line Of Code Used For The Sum Of The OUTPUT SGST Amount For All sold
			// Goods
			osgst += bookDetails.getBookDetailsSgstAmount();

			// The Line Of Code Used For The Sum Of The OUTPUT IGST Amount For All sold
			// Goods
			igst += bookDetails.getBookDetailsIgstAmount();

			// The Line Of Code Used For The Sum Of The Cost Amount For All sold Goods
			totalCreditAmount += bookDetails.getBookDetailsParticularAmount();

		}
		/**
		 * 1. Credit Book Initialization
		 */

		List<Books> listBooks = new ArrayList<>();

		Books credit_Account_1 = new Books();

		// Here "Sales Account" Is The Default Credit Transactional Accounts
		credit_Account_1.setBooksAccount_Ref(accountsServices.getAccountIdByAccountName("Sales Account"));

		// Here "Direct Income" Is The Category Of "Sales Account"
		credit_Account_1
				.setBooksAccountType_Ref(accountsTypeServices.getAccTypeId_ByName("Direct Income").getAccountTypeId());
		credit_Account_1.setBooksDate(books.getTransectionDate());
		credit_Account_1.setBooksCreditAmount(totalCreditAmount);
		credit_Account_1.setBooksDebitAmount(0d);

		listBooks.add(credit_Account_1);

		if (ocgst != 0) {
			Books credit_Account_2 = new Books();
			// Here "OUTPUT CGST" Will Be The 2nd Credit Transaction Account If Its Applied
			credit_Account_2.setBooksAccount_Ref(accountsServices.getAccountIdByAccountName("OUTPUT CGST"));
			credit_Account_2.setBooksAccountType_Ref(
					accountsTypeServices.getAccTypeId_ByName("Current Liability").getAccountTypeId());
			credit_Account_2.setBooksDate(books.getTransectionDate());
			credit_Account_2.setBooksCreditAmount(ocgst);
			credit_Account_2.setBooksDebitAmount(0d);

			listBooks.add(credit_Account_2);

		}
		if (osgst != 0) {
			Books credit_Account_3 = new Books();

			// Here "OUTPUT SGST" Will Be The 3rd Credit Transaction Account If Its Applied
			credit_Account_3.setBooksAccount_Ref(accountsServices.getAccountIdByAccountName("OUTPUT SGST"));
			// Here "Direct Income" Is The Category Of "OUTPUT SGST" Account
			credit_Account_3.setBooksAccountType_Ref(
					accountsTypeServices.getAccTypeId_ByName("Current Liability").getAccountTypeId());
			credit_Account_3.setBooksDate(books.getTransectionDate());
			credit_Account_3.setBooksCreditAmount(osgst);
			credit_Account_3.setBooksDebitAmount(0d);

			listBooks.add(credit_Account_3);
		}
		if (igst != 0) {
			Books credit_Account_4 = new Books();
			// Here "OUTPUT CGST" Will Be The 3rd Credit Transaction Account If Its Applied
			credit_Account_4.setBooksAccount_Ref(accountsServices.getAccountIdByAccountName("OUTPUT IGST"));
			// Here "Direct Income" Is The Category Of "OUTPUT IGST" Account
			credit_Account_4.setBooksAccountType_Ref(
					accountsTypeServices.getAccTypeId_ByName("Current Liability").getAccountTypeId());
			credit_Account_4.setBooksDate(books.getTransectionDate());
			credit_Account_4.setBooksCreditAmount(igst);
			credit_Account_4.setBooksDebitAmount(0d);
			listBooks.add(credit_Account_4);
		}
		/**
		 * 2. Debit Book Initialization
		 */

		Books debit_account_1 = new Books();
		// Here "Sundry Debtors" Is The Debit Transaction Accounts Used When Goods Are
		// Sales On Debit
		if (books.getTransectionalAccounts().getTransactionAccountName().equals("Sundry Debtors")) {
//			debit_account_1.setBooksAccount_Ref(books.getAccountId());
			debit_account_1.setBooksAccount_Ref(accountsServices.getAccountIdByAccountName("Factory Account"));
			// Here "Current Assets" Is The Category Of "Sundry Debtors" Accounts Used When
			// Goods Are Sold On Debit
			debit_account_1.setBooksAccountType_Ref(
					accountsTypeServices.getAccTypeId_ByName("Sundry Debtors").getAccountTypeId());
		} else {
			// If The Transaction Is Not "Sundry Debtors" Then User Given Account Will Be
			// Treated As Debit Account When Goods Are Sold By (Cash A/C Or Bank A/C)
			debit_account_1.setBooksAccount_Ref(accountsServices
					.getAccountIdByAccountName(books.getTransectionalAccounts().getTransactionAccountName()));
			// Here "Direct Income" Is The Category Of Cash/Bank Related Transaction Used
			// When Goods Are Sold By Cash/Bank
			debit_account_1.setBooksAccountType_Ref(
					accountsTypeServices.getAccTypeId_ByName("Current Assets").getAccountTypeId());
		}

		debit_account_1.setBooksDate(books.getTransectionDate());
		debit_account_1.setBooksCreditAmount(0d);
		debit_account_1.setBooksDebitAmount(books.getTransectionalAccounts().getTransactionAmount());
		listBooks.add(debit_account_1);
// Books Ready
		/**
		 * creadit and debit book separation for ladger
		 */
		List<Books> creditBooks = new ArrayList<>();
		Books debitBook = new Books();
		for (Books filterBook : listBooks) {
			if (filterBook.getBooksCreditAmount() != 0) {
				// Identifying The All Credit Ledger
				creditBooks.add(filterBook);
			} else {
				// Identifying The All Debit Ledger
				debitBook.setBooksAccount_Ref(filterBook.getBooksAccount_Ref());
				debitBook.setBooksAccountType_Ref(filterBook.getBooksAccountType_Ref());
				debitBook.setBooksDate(filterBook.getBooksDate());
				debitBook.setBooksDebitAmount(filterBook.getBooksDebitAmount());
				debitBook.setBooksCreditAmount(filterBook.getBooksCreditAmount());
			}
		}
		/**
		 * Update InventoryItems 1. Get InventoryItems 1. Update OutwardQty Of
		 * InventoryItems
		 */
		List<InventoryItems> items = new ArrayList<>();
		for (int i = 0; i < books.getInventoryJournal().size(); i++) {
			InventoryItems item = new InventoryItems();
			item = inventoryItemsService.getInventoryItemByiiId(books.getInventoryJournal().get(i).getIj_iiId());
			item.setIiQty((item.getIiQty()) - books.getInventoryJournal().get(i).getIjJOutwardQty());
			items.add(item);
		}
//InventoryItems Ready

		/**
		 * Initialize BookInfo
		 */
		BookInfo bookInfo = new BookInfo();
		// The Line Of Code Used For Identifying The Book Type
		if (books.getTransectionalAccounts().getTransactionAccountName().equals("Sundry Debtors")) {
			bookInfo.setBookInfoType_Ref(bookTypeServices.getBookTypeId("Sales Book").getBookTypeId());
		} else {
			// The Line Of Code Used For Identifying The Book Type
			bookInfo.setBookInfoType_Ref(bookTypeServices.getBookTypeId("Cash Book").getBookTypeId());
		}

		bookInfo.setBookInfoUser_Ref(books.getUserId());
//		bookInfo.setBookInfoAccount_Ref(books.getAccountId());
		bookInfo.setBookInfoAccount_Ref(accountsServices.getAccountIdByAccountName("Factory Account"));
		bookInfo.setBookInfoNarration("Sales(BOM)");
		bookInfo.setBookInfoDate(books.getTransectionDate());
		bookInfo.setBooks(listBooks);
		bookInfo.setInventoryJournals(books.getInventoryJournal());
		bookInfo.setBookDetails(books.getBookDetails());
//		if (books.getEmi().size() != 0) {//skip to add emi
//			bookInfo.setEmi(books.getEmi());// Not Required because bom is a internal transection
//		}

// BookInfo Ready
		/**
		 * Initializing Credit Ledgers for posting
		 */
		List<Ledger> list_Of_Ledger = new ArrayList<>();
		for (Books tempBook : creditBooks) {
			List<LedgerDetails> list_Of_Credit_Ledger_Details = new ArrayList<>();
			// All Credit Account Post On Ledger
			Ledger credit_Ledger = new Ledger();
			LedgerDetails credit_Ledger_Details = new LedgerDetails();
			credit_Ledger.setLedgerAccount_Ref(tempBook.getBooksAccount_Ref());
			credit_Ledger.setLedgerAccountType_Ref(tempBook.getBooksAccountType_Ref());
			credit_Ledger.setLedgerDate(books.getTransectionDate());
			credit_Ledger.setLedgerUser_Ref(books.getUserId());

			credit_Ledger_Details.setLedgerDetailsDate(tempBook.getBooksDate());
			credit_Ledger_Details.setLedgerDetailsAccount_Ref(debitBook.getBooksAccount_Ref());// from debit account
			credit_Ledger_Details.setLedgerDetailsAccountType_Ref(debitBook.getBooksAccountType_Ref());// from debit
																										// account
			credit_Ledger_Details.setLedgerDetailsDebitAmount(0d);
			credit_Ledger_Details.setLedgerDetailsCreditAmount(tempBook.getBooksCreditAmount());
			list_Of_Credit_Ledger_Details.add(credit_Ledger_Details);
			credit_Ledger.setLedgerDetails(list_Of_Credit_Ledger_Details);
			list_Of_Ledger.add(credit_Ledger);
		}
		/**
		 * Initializing Debit Ledgers for posting
		 */
		List<LedgerDetails> list_Of_Debit_Ledger_Details = new ArrayList<>();
		{
			Ledger debit_Ledger = new Ledger();
			debit_Ledger.setLedgerAccount_Ref(debitBook.getBooksAccount_Ref());
			debit_Ledger.setLedgerAccountType_Ref(debitBook.getBooksAccountType_Ref());
			debit_Ledger.setLedgerDate(books.getTransectionDate());
			debit_Ledger.setLedgerUser_Ref(books.getUserId());
			for (Books tempBook : creditBooks) {
				LedgerDetails credit_Ledger_Details = new LedgerDetails();
				credit_Ledger_Details.setLedgerDetailsDate(tempBook.getBooksDate());// from credit account
				credit_Ledger_Details.setLedgerDetailsAccount_Ref(tempBook.getBooksAccount_Ref());// from debit account
				credit_Ledger_Details.setLedgerDetailsAccountType_Ref(tempBook.getBooksAccountType_Ref());// from debit
																											// account
				credit_Ledger_Details.setLedgerDetailsDebitAmount(tempBook.getBooksCreditAmount());
				credit_Ledger_Details.setLedgerDetailsCreditAmount(0d);

				list_Of_Debit_Ledger_Details.add(credit_Ledger_Details);
			}
			debit_Ledger.setLedgerDetails(list_Of_Debit_Ledger_Details);
			list_Of_Ledger.add(debit_Ledger);
		}

		List<BookInfo> newBookInfo = new ArrayList<>();
		newBookInfo.add(bookInfo);
		FolioNumber folioNo = new FolioNumber();
		folioNo.setFolioNarration(books.getNaration());
//		if (books.getProduct().size() != 0) {//skip to add product
//			folioNo.setProduct(books.getProduct());// Not Required because bom is a internal transection
//		}
		folioNo.setBookInfos(newBookInfo);// Add BookInfo
		folioNo.setLedger(list_Of_Ledger);// Add Leaser
		inventoryItemsService.addInventoryItemsList(items);// update inventory items qty after consume items in to bom
		folioNumberServices.addFolio(folioNo);// adding folio
		bomService.saveBom(books.getBom());
		InventoryItems Bomitem = new InventoryItems();
		Bomitem = inventoryItemsService.getInventoryItemByiiId(books.getBom().getBomItemsId_Ref());
		Bomitem.setIiQty((Bomitem.getIiQty()) + books.getBom().getQty());
		inventoryItemsService.addInventoryItems(Bomitem);// update inventory items qty after create bom
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	public FolioNumber addFolio(FolioNumber folioNumber) {
		return folioNumberServices.addFolioNumber(folioNumber);
	}

//	{
//		  "bomItemsId_Ref": 123,
//		  "qty": 5,
//		  "status": "active",
//		  "bomList": [
//		    {
//		      "bomListId": 1,
//		      "bomListBomId_Ref": 456,
//		      "bomListItemsId_Ref": 789
//		    },
//		    {
//		      "bomListId": 2,
//		      "bomListBomId_Ref": 789,
//		      "bomListItemsId_Ref": 123
//		    }
//		  ],
//		  "extraCost": [
//		    {
//		      "etraCostId": 1,
//		      "etraCostBomId_Ref": 456,
//		      "costParticular": "Material Cost",
//		      "sgst": 5,
//		      "cgst": 5,
//		      "igst": 0,
//		      "sgstAmount": 50.0,
//		      "cgstAmount": 50.0,
//		      "igstAmount": 0.0,
//		      "costParticularAmount": 1000.0
//		    }
//		  ]
//		}

}