package com.accounting.rest.multitenant.tenant.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accounting.rest.exception.BooksTypeNotFoundException;
import com.accounting.rest.multitenant.tenant.entity.BookType;
import com.accounting.rest.multitenant.tenant.post.service.PostBookType;
import com.accounting.rest.multitenant.tenant.repository.BookTypeRepo;

@Service
//@Transactional()//Use For Single Database
@Transactional("tenantTransactionManager")//Use For Multitenant
public class BookTypeServices {
	@Autowired
	private  BookTypeRepo bookTypeRepo;

	@Autowired
	public PostBookType postBookType;

//	@PostConstruct
	public void BookTypeList() {
		postBookType.BookAccountsList();
	}



	public Optional<BookType> getBookTypeByName(String typeName) {
		BookType ac = new BookType();
		ac = bookTypeRepo.getBookByName(typeName);
		Optional<BookType> opt = Optional.ofNullable(ac);
		return opt;

	}

//Add BookType

	public BookType addBookType(BookType bookType) {
		return bookTypeRepo.save(bookType);

	}

//Find All BookType
//	public List<GenericsBookType> findAllBookType() {
//		List<GenericsBookType> bookTytpe = new ArrayList<GenericsBookType>();
//		List<BookType> bookTypeList = bookTypeRepo.findAll();
//		for (BookType bookTypes : bookTypeList) {
//			GenericsBookType newBook = new GenericsBookType();
//			newBook.setBookTypeId(bookTypes.getBookTypeId());
//			newBook.setBookTypeName(bookTypes.getBookTypeName());
//			bookTytpe.add(newBook);
//		}
//		return bookTytpe;
//	}

	// Find Accounts By Name
	public BookType getBookTypeId(String typeName) {
		return bookTypeRepo.getBookByName(typeName);

	}

//Find BookType By Id
	public BookType findBookTypeById(Long id) {
		return bookTypeRepo.findById(id).orElseThrow(() -> new BooksTypeNotFoundException(""));

	}

//Update BookType

//Delete BookType By Id
	public void deleteBookTypeById(Long id) {

		bookTypeRepo.deleteById(id);

	}

}
