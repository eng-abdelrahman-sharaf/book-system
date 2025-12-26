package org.example.backend.mapper;

import org.example.backend.model.dto.BookCreateRequest;
import org.example.backend.model.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookRequestMapper {

	public Book toNewBook(BookCreateRequest request) {
		Book book = new Book();
		book.setIsbn(request.getIsbn());
		book.setTitle(request.getTitle());
		book.setPublisherId(request.getPublisherId());
		book.setPublicationYear(request.getPublicationYear());
		book.setSellingPrice(request.getSellingPrice());
		book.setCategory(request.getCategory());
		book.setNumberOfBooks(request.getNumberOfBooks() != null ? request.getNumberOfBooks() : 0);
		book.setThreshold(request.getThreshold() != null ? request.getThreshold() : 5);
		return book;
	}

	public Book toExistingBook(String isbn, BookCreateRequest request) {
		Book book = new Book();
		book.setIsbn(isbn);
		book.setTitle(request.getTitle());
		book.setPublisherId(request.getPublisherId());
		book.setPublicationYear(request.getPublicationYear());
		book.setSellingPrice(request.getSellingPrice());
		book.setCategory(request.getCategory());
		book.setNumberOfBooks(request.getNumberOfBooks());
		book.setThreshold(request.getThreshold());
		return book;
	}
}
