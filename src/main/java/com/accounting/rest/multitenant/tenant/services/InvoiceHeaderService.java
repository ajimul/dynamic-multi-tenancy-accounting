package com.accounting.rest.multitenant.tenant.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accounting.rest.multitenant.tenant.entity.InvoiceHeader;
import com.accounting.rest.multitenant.tenant.repository.InvoiceHeaderRepo;

@Service
//@Transactional()//Use For Single Database
@Transactional("tenantTransactionManager")//Use For Multitenant
public class InvoiceHeaderService {
	private final InvoiceHeaderRepo invoiceHeaderRepository;

	@Autowired
	public InvoiceHeaderService(InvoiceHeaderRepo invoiceHeaderRepository) {
		this.invoiceHeaderRepository = invoiceHeaderRepository;
	}

	public void saveInvoiceHeader(InvoiceHeader newInvoiceHeader) {
		// You can add validation or additional logic as needed
		invoiceHeaderRepository.save(newInvoiceHeader);
	}

	public void editInvoiceHeader(InvoiceHeader updatedInvoiceHeader) {
		// You can add validation or additional logic as needed
		invoiceHeaderRepository.save(updatedInvoiceHeader);
	}

	public List<InvoiceHeader> getAllInvoiceHeaders() {

		return invoiceHeaderRepository.findAll();
	}
}
