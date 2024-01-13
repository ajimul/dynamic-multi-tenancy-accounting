package com.accounting.rest.multitenant.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accounting.rest.multitenant.dto.InvoiceDto;
import com.accounting.rest.multitenant.tenant.entity.InvoiceHeader;
import com.accounting.rest.multitenant.tenant.services.AccountsServices;
import com.accounting.rest.multitenant.tenant.services.FolioNumberServices;
import com.accounting.rest.multitenant.tenant.services.InvoiceHeaderService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@RestController
//@CrossOrigin(origins = "https://spotsolution.store")
@RequestMapping("api/single/report")
public class SingleReportController {
	private final AccountsServices accountsServices;
	private final InvoiceHeaderService invoiceHeaderService;
	@Autowired
	private FolioNumberServices folioNumberServices;

	@Autowired
	public SingleReportController(AccountsServices accountsServices, InvoiceHeaderService invoiceHeaderService) {
		super();
		this.accountsServices = accountsServices;
		this.invoiceHeaderService = invoiceHeaderService;

	}

//	@GetMapping("/{FolioNo}")
//	public ResponseEntity<byte[]> Print(@PathVariable Long FolioNo) throws JRException {
//
//		InvoiceDto invoice = new InvoiceDto();
//		invoice = folioNumberServices.getInvoiceByFolioId(FolioNo);
//
////		String masterReportFileName = "src/main/resources/sub_invoice.jrxml";
//		String masterReportFileName = "/sub_invoice.jrxml";
//
//		List<InvoiceHeader> invoiceHeader = new ArrayList<InvoiceHeader>();
//		invoiceHeader = invoiceHeaderService.getAllInvoiceHeaders();
//
//		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(invoice.getInvoiceJRBeanDataSource());
//		JasperPrint jasperPrint = new JasperPrint();
////		LocalDate booksDate = LocalDate.now();
//		java.sql.Date sqlDate = java.sql.Date.valueOf(invoice.getInvoiceJRParameter().getInvoiceDate());
////		java.sql.Date sqlDate = java.sql.Date.valueOf(booksDate);
//		try {
//			JasperReport compailReport = JasperCompileManager.compileReport(masterReportFileName);
//			Map<String, Object> parameters = new HashMap<String, Object>();
//			parameters.put("accountName", invoice.getInvoiceJRParameter().getPartyName());
//			parameters.put("headText", invoiceHeader.get(0).getOrganizationName().toString());
//			parameters.put("headerContactDetails", invoiceHeader.get(0).getContactDetails().toString());
//			parameters.put("InvoiceNo", invoice.getInvoiceJRParameter().getFolioId().toString());
//			parameters.put("InvoiceDate", sqlDate);
//			parameters.put("Bill_Address", invoice.getInvoiceJRParameter().getPartyBillingAddress());
//			parameters.put("Shipping_Address", invoice.getInvoiceJRParameter().getPartyShipingAddress());
//
//			jasperPrint = JasperFillManager.fillReport(compailReport, parameters, dataSource);
//
//		} catch (JRException e) {
//			System.out.println(e);
//
//			e.printStackTrace();
//		}
//
//		byte data[] = JasperExportManager.exportReportToPdf(jasperPrint);
//
//		System.err.println(data);
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");
//		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
//	}

	@GetMapping("/{FolioNo}")
	public ResponseEntity<byte[]> Print(@PathVariable Long FolioNo) throws JRException {

		InvoiceDto invoice = folioNumberServices.getInvoiceByFolioId(FolioNo);

		// Load JRXML file from classpath
		InputStream jrxmlStream = getClass().getResourceAsStream("/sub_invoice.jrxml");

		if (jrxmlStream == null) {
			throw new JRException("JRXML file not found");
		}

		List<InvoiceHeader> invoiceHeader = invoiceHeaderService.getAllInvoiceHeaders();

		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(invoice.getInvoiceJRBeanDataSource());
		JasperPrint jasperPrint;

		try {
			JasperReport compileReport = JasperCompileManager.compileReport(jrxmlStream);
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("accountName", invoice.getInvoiceJRParameter().getPartyName());
			parameters.put("headText", invoiceHeader.get(0).getOrganizationName().toString());
			parameters.put("headerContactDetails", invoiceHeader.get(0).getContactDetails().toString());
			parameters.put("InvoiceNo", invoice.getInvoiceJRParameter().getFolioId().toString());
			parameters.put("InvoiceDate", java.sql.Date.valueOf(invoice.getInvoiceJRParameter().getInvoiceDate()));
			parameters.put("Bill_Address", invoice.getInvoiceJRParameter().getPartyBillingAddress());
			parameters.put("Shipping_Address", invoice.getInvoiceJRParameter().getPartyShipingAddress());

			jasperPrint = JasperFillManager.fillReport(compileReport, parameters, dataSource);
		} finally {
			try {
				if (jrxmlStream != null) {
					jrxmlStream.close();
				}
			} catch (IOException e) {
				// Handle or log the IOException if necessary
			}
		}

		byte data[] = JasperExportManager.exportReportToPdf(jasperPrint);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
	}

}
