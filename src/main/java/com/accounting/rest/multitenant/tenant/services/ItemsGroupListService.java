package com.accounting.rest.multitenant.tenant.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accounting.rest.multitenant.tenant.entity.ItemsGroupList;
import com.accounting.rest.multitenant.tenant.repository.ItemsGroupListRepo;

@Service
//@Transactional()//Use For Single Database
@Transactional("tenantTransactionManager")//Use For Multitenant
public class ItemsGroupListService {

	private final ItemsGroupListRepo itemsGroupListRepo;

	@Autowired
	public ItemsGroupListService(ItemsGroupListRepo itemsGroupListRepo) {
		super();
		this.itemsGroupListRepo = itemsGroupListRepo;
	}

	public List<ItemsGroupList> getItemsGroupList() {
		return itemsGroupListRepo.findAll();
	}

	public void addItemGroup(String groupName) {
		ItemsGroupList newGroup = new ItemsGroupList();
		newGroup.setIgName(groupName);
		itemsGroupListRepo.save(newGroup);
	}

}
