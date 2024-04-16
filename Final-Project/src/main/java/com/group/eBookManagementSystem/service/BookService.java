package com.group.eBookManagementSystem.service;

import com.group.eBookManagementSystem.model.Book;
import com.group.eBookManagementSystem.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CustomerService customerService;

    public void addBook(Book book) {
        bookRepository.save(book);
    }


    public Iterable<Book> getBooks() {
        return bookRepository.findAll();
    }

    public Book findBookById(Integer id) {
        return bookRepository.findBookById(id);
    }

    public void updateBook(Integer id, Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Integer id) {
        bookRepository.deleteBookById(id);
        customerService.removeBookForAllCustomers(id);
    }

    public void rateBook(Integer id, Integer rate) {
        Book book = bookRepository.findBookById(id);
        book.addRate(rate);
        bookRepository.save(book);
    }

}
