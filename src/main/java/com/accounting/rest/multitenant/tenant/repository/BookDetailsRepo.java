package com.accounting.rest.multitenant.tenant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.accounting.rest.multitenant.tenant.entity.BookDetails;

public interface BookDetailsRepo extends JpaRepository<BookDetails, Long> {

	@Query("select u from BookDetails u where u.bookDetailsBookInfo_Ref=:bookDetailsBookInfo_Ref")
	public List<BookDetails> findByBookDetailsBookInfo_Ref(
			@Param("bookDetailsBookInfo_Ref") Long bookDetailsBookInfo_Ref);
}
