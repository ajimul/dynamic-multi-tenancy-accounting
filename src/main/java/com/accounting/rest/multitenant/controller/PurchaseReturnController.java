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

import com.accounting.rest.multitenant.dto.PurchaseModel;
import com.accounting.rest.multitenant.tenant.entity.BookDetails;
import com.accounting.rest.multitenant.tenant.entity.BookInfo;
import com.accounting.rest.multitenant.tenant.entity.Books;
import com.accounting.rest.multitenant.tenant.entity.FolioNumber;
import com.accounting.rest.multitenant.tenant.entity.InventoryItems;
import com.accounting.rest.multitenant.tenant.entity.Ledger;
import com.accounting.rest.multitenant.tenant.entity.LedgerDetails;
import com.accounting.rest.multitenant.tenant.services.AccountsServices;
import com.accounting.rest.multitenant.tenant.services.AccountsTypeServices;
import com.accounting.rest.multitenant.tenant.services.BookTypeServices;
import com.accounting.rest.multitenant.tenant.services.FolioNumberServices;
import com.accounting.rest.multitenant.tenant.services.InventoryItemsService;

/**
 * Implementation For... 1. Folio No. 2. BooksInfo 3. Books 4. BooksDetails 5.
 * Ledgers 6. LedgersDetails
 * 
 * Relationship Of Schema: Folio Is The Parent Of BookInfo And Relationship Is
 * (1:1) BookInfo Is The Parent Of Books And Relationship Is (1:N) Again
 * BookInfo Is The Parent Of BooksDetails And Relationship Is (1:N)
 * 
 * Again Folio Is The Parent Of Ledger And Relationship Is (1:1) Ledger Is The
 * Parent Of BooksDetails And Relationship Is (1:N)
 * 
 * Description For Implementation: Prerequisite:( Needs Proper Knowledge Of
 * Accountancy)
 * 
 * Folio Is The Number Help To Find Out The Transaction Between Books And
 * Ledgers
 * 
 * Books Are The Record For All The Debit And Credit Accounts And Ledger Are The
 * Record For Each Single Accounts Whether Accounts Are Debit Or Credit
 * 
 * According To Books Each Accounts Should Be Post On The Ledger And Each
 * Ledgers Accounts May Have Different Roll For Each To Others Accounts A Single
 * Credit Account May Have Effect On Multiple Debit Accounts Depend On
 * Transaction Or Vice Versa To Others Accounts.
 * 
 * Every Transaction Must Be 1. Debit Account To Credit Account (Recommended) 2.
 * List<Debit> Account To Credit Account (Recommended) 3. List<Debit> Accounts
 * To List<Credit> Accounts
 * 
 * In This Package I Will Implement The Second One( 2. List<Debit> Accounts To
 * Credit Account (Recommended) ) Every Transaction Should Be Two Part 1. Books
 * (Books Of Original Entry) 2. After Books Entry Is Complete Then Its Should Be
 * Post On Ledgers
 * 
 * 
 * @author admin
 *
 */


@RestController
//@Transactional()//Use For Single Database
@Transactional("tenantTransactionManager")//Use For Multitenant
//@CrossOrigin(origins = "https://spotsolution.store")
@RequestMapping("api/purchase/return")
@Scope("prototype")
public class PurchaseReturnController {

	private final FolioNumberServices folioNumberServices;
	private final AccountsTypeServices accountsTypeServices;
	private final AccountsServices accountsServices;
	private final BookTypeServices bookTypeServices;
	private final InventoryItemsService inventoryItemsService;

	@Autowired
	public PurchaseReturnController(FolioNumberServices folioNumberServices, AccountsTypeServices accountsTypeServices,
			AccountsServices accountsServices, BookTypeServices bookTypeServices,
			InventoryItemsService inventoryItemsService) {
		super();
		this.folioNumberServices = folioNumberServices;
		this.accountsTypeServices = accountsTypeServices;
		this.accountsServices = accountsServices;
		this.bookTypeServices = bookTypeServices;
		this.inventoryItemsService = inventoryItemsService;
	}

	Double icgst = 0d;
	Double isgst = 0d;
	Double igst = 0d;
	Double totalCreditAmount = 0d;

	@PostMapping("/add")
	public ResponseEntity<String> addBooks(@RequestBody PurchaseModel books) {

		/**
		 * GST Separation
		 */

		for (BookDetails bookDetails : books.getBookDetails()) {
			// The Line Of Code Used For The Sum Of The icgst Amount For All sold Goods
			icgst += bookDetails.getBookDetailsCgstAmount();

			// The Line Of Code Used For The Sum Of The isgst Amount For All sold Goods
			isgst += bookDetails.getBookDetailsSgstAmount();

			// The Line Of Code Used For The Sum Of The igst Amount For All sold Goods
			igst += bookDetails.getBookDetailsIgstAmount();

			// The Line Of Code Used For The Sum Of The Cost Amount For All sold Goods
			totalCreditAmount += bookDetails.getBookDetailsParticularAmount();

		}

		/**
		 * 1. Credit Book Initialization
		 */
		List<Books> listBooks = new ArrayList<Books>();

		Books credit_Account_1 = new Books();

		// Here "Purchase Return Account" Is The Default Credit Transactional Accounts
		credit_Account_1.setBooksAccount_Ref(accountsServices.getAccountIdByAccountName("Purchase Return Account"));

		// Here "Direct Expenses" Is The Category Of "Sales Account"
		credit_Account_1.setBooksAccountType_Ref(
				accountsTypeServices.getAccTypeId_ByName("Direct Expenses").getAccountTypeId());
		credit_Account_1.setBooksDate(books.getTransectionDate());
		credit_Account_1.setBooksCreditAmount(totalCreditAmount);
		credit_Account_1.setBooksDebitAmount(0d);

		listBooks.add(credit_Account_1);

		if (icgst != 0) {
			Books credit_Account_2 = new Books();
			// Here "INPUT CGST" Will Be The 2nd Credit Transaction Account If Its Applied
			credit_Account_2.setBooksAccount_Ref(accountsServices.getAccountIdByAccountName("INPUT CGST"));
			credit_Account_2.setBooksAccountType_Ref(
					accountsTypeServices.getAccTypeId_ByName("Current Assets").getAccountTypeId());
			credit_Account_2.setBooksDate(books.getTransectionDate());
			credit_Account_2.setBooksCreditAmount(icgst);
			credit_Account_2.setBooksDebitAmount(0d);

			listBooks.add(credit_Account_2);

		}
		if (isgst != 0) {
			Books credit_Account_3 = new Books();

			// Here "INPUT SGST" Will Be The 3rd Credit Transaction Account If Its Applied
			credit_Account_3.setBooksAccount_Ref(accountsServices.getAccountIdByAccountName("INPUT SGST"));
			// Here "Direct Income" Is The Category Of "OUTPUT SGST" Account
			credit_Account_3.setBooksAccountType_Ref(
					accountsTypeServices.getAccTypeId_ByName("Current Assets").getAccountTypeId());
			credit_Account_3.setBooksDate(books.getTransectionDate());
			credit_Account_3.setBooksCreditAmount(isgst);
			credit_Account_3.setBooksDebitAmount(0d);

			listBooks.add(credit_Account_3);
		}
		if (igst != 0) {
			Books credit_Account_4 = new Books();

			// Here "INPUT CGST" Will Be The 3rd Credit Transaction Account If Its Applied
			credit_Account_4.setBooksAccount_Ref(accountsServices.getAccountIdByAccountName("INPUT IGST"));

			// Here "Direct Income" Is The Category Of "OUTPUT IGST" Account
			credit_Account_4.setBooksAccountType_Ref(
					accountsTypeServices.getAccTypeId_ByName("Current Assets").getAccountTypeId());
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
			debit_account_1.setBooksAccount_Ref(accountsServices.getAccountIdByAccountName(books.getPartyAcName()));
			// Here "Current Assets" Is The Category Of "Sundry Debtors" Accounts Used When
			// Goods Are Sold On Debit
			debit_account_1.setBooksAccountType_Ref(
					accountsTypeServices.getAccTypeId_ByName("Current Assets").getAccountTypeId());

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

		List<Books> creditBooks = new ArrayList<Books>();

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
		List<InventoryItems> items = new ArrayList<InventoryItems>();
		for (int i = 0; i < books.getInventoryJournal().size(); i++) {
			InventoryItems item = new InventoryItems();
			item = inventoryItemsService.getInventoryItemByiiId(books.getInventoryJournal().get(i).getIj_iiId());
			item.setIiQty(item.getIiQty() - books.getInventoryJournal().get(i).getIjJOutwardQty());
			items.add(item);
		}
//InventoryItems Ready

		/**
		 * Initialize BookInfo
		 */

		BookInfo bookInfo = new BookInfo();
		// The Line Of Code Used For Identifying The Book Type
		if (books.getTransectionalAccounts().getTransactionAccountName().equals("Sundry Debtors")) {
			bookInfo.setBookInfoType_Ref(bookTypeServices.getBookTypeId("Purchase Return Book").getBookTypeId());
		} else {
			// The Line Of Code Used For Identifying The Book Type
			bookInfo.setBookInfoType_Ref(bookTypeServices.getBookTypeId("Cash Book").getBookTypeId());
		}
		bookInfo.setBookInfoUser_Ref(books.getUserId());
		bookInfo.setBookInfoAccount_Ref(books.getAccountId());
		bookInfo.setBookInfoNarration("Purchase Return");
		bookInfo.setBookInfoDate(books.getInvoiceDate());
		bookInfo.setBooks(listBooks);
		bookInfo.setInventoryJournals(books.getInventoryJournal());
		bookInfo.setBookDetails(books.getBookDetails());

		// BookInfo Ready
		/**
		 * Initializing Credit Ledgers for posting
		 */
		List<Ledger> list_Of_Ledger = new ArrayList<Ledger>();

		for (Books tempBook : creditBooks) {

			List<LedgerDetails> list_Of_Credit_Ledger_Details = new ArrayList<LedgerDetails>();

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

		List<LedgerDetails> list_Of_Debit_Ledger_Details = new ArrayList<LedgerDetails>();

		{
			// All Debit Account Post On Ledger
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

		List<BookInfo> newBookInfo = new ArrayList<BookInfo>();
		newBookInfo.add(bookInfo);
		FolioNumber folioNo = new FolioNumber();
		folioNo.setFolioNarration(books.getNaration());
		folioNo.setBookInfos(newBookInfo);// Add BookInfo
		folioNo.setLedger(list_Of_Ledger);// Add Leaser
		inventoryItemsService.addInventoryItemsList(items);// update inventory items qty
		folioNumberServices.addFolio(folioNo);// adding folio
		return new ResponseEntity<>(HttpStatus.CREATED);

	}

	public FolioNumber addFolio(FolioNumber folioNumber) {
		return folioNumberServices.addFolioNumber(folioNumber);
	}

}
