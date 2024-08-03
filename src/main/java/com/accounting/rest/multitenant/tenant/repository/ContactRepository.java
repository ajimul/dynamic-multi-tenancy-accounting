package com.accounting.rest.multitenant.tenant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.accounting.rest.multitenant.tenant.entity.ContactEntity;

public interface ContactRepository extends JpaRepository<ContactEntity, Long>,ContactEntityRepositoryImpl  {

//	@Query(value = "SELECT * FROM" + DBContextHolder.getCurrentDb()
//			+ ".contactentity WHERE name = :name", nativeQuery = true)
//	ContactEntity GetContactEntity(@Param("name") String name);
//
//	List<ContactEntity> findByName(String name);


//
//	String DYNAMIC_DB_QUERY = "SELECT * FROM %s "+DBContextHolder.getCurrentDb()+".contactentity WHERE name = :name";
//
//    @Query(value = DYNAMIC_DB_QUERY, nativeQuery = true)
//    ContactEntity GetContactEntity(@Param("name") String name);


    @Query(value = "SELECT c FROM #{#entityName} c WHERE c.name = :name")
    ContactEntity GetContactEntity(@Param("name") String name);

    List<ContactEntity> findByName(String name);

}