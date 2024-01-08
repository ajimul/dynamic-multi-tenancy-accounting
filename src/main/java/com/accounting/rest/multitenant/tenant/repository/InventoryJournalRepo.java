package com.accounting.rest.multitenant.tenant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.accounting.rest.multitenant.tenant.entity.InventoryJournal;

public interface InventoryJournalRepo extends JpaRepository<InventoryJournal, Long> {
	@Query("select u from inventory_journal u where u.ijVoucherNo = :voucherId")
	List<InventoryJournal> getInventoryJournal_ByVoucherId(@Param("voucherId") Long voucherId);

}
