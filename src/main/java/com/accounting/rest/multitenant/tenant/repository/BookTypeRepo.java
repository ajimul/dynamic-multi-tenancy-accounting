package com.accounting.rest.multitenant.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.accounting.rest.multitenant.tenant.entity.BookType;
import com.accounting.rest.multitenant.tenant.entity.Users;

public interface BookTypeRepo extends JpaRepository<BookType, Long> {
	 BookType findByBookTypeId(Long bookTypeId);
	
	@Query("select u from BookType u where u.bookTypeName = :bookTypeName")
	BookType getBookByName(@Param("bookTypeName") String bookTypeName);

}
