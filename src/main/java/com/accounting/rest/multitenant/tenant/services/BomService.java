package com.accounting.rest.multitenant.tenant.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accounting.rest.multitenant.tenant.entity.Bom;
import com.accounting.rest.multitenant.tenant.repository.BomRepo;

@Service
//@Transactional()//Use For Single Database
@Transactional("tenantTransactionManager") // Use For Multitenant
public class BomService {

	private final BomRepo bomRepository;

	@Autowired
	public BomService(BomRepo bomRepository) {
		this.bomRepository = bomRepository;
	}

	public void saveBom(Bom bom) {
		bomRepository.save(bom);
	}

	public Bom getBomById(Long bomId) {
		return bomRepository.findById(bomId).orElse(null);
	}

}
