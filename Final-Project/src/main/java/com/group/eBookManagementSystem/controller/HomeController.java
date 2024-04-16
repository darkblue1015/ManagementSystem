package com.group.eBookManagementSystem.controller;

import com.group.eBookManagementSystem.model.Book;
import com.group.eBookManagementSystem.model.Customer;
import com.group.eBookManagementSystem.service.BookService;
import com.group.eBookManagementSystem.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    // autowire the customerService object using dependency injection
    @Autowired
    private CustomerService customerService;

    // define a POST method to add a new customer to the system
    @PostMapping("/addCustomer")
    public String addCustomer(@RequestBody Customer customer) throws Exception {
        customerService.addCustomer(customer);
        return "Add new customer successfully!";
    }

    // define a GET method to retrieve all the customers
    @GetMapping("/listCustomers")
    public Iterable<Customer> getCustomers() {
        return customerService.getCustomers();
    }

    // define a GET method to find a customer by their username
    @GetMapping("/findCustomerByUserName/{userName}")
    public Customer findCustomerByUserName(@PathVariable String userName) {
        return customerService.findCustomerByUserName(userName);
    }

    // define a POST method to update an existing customer
    @PostMapping("/updateCustomerByUserName/{userName}")
    public String updateCustomer(@PathVariable String userName, @RequestBody Customer customer) {
        customerService.updateCustomer(userName, customer);

        return "Updated customer with given UserName: " + userName;
    }

    // define a POST method to delete an existing customer
    @PostMapping("/deleteCustomer")
    public String deleteCustomer(@RequestParam String userName) {
        customerService.deleteCustomer(userName);
        return "Delete the customer successfully!";
    }

    // autowire the bookService object using dependency injection
    @Autowired
    private BookService bookService;

    // define a POST method to add a new book to the system
    @PostMapping("/addBook")
    public String addBook(@RequestBody Book book) {
        bookService.addBook(book);
        return "Add new book successfully!";
    }

    // define a GET method to retrieve all the books from the system
    @GetMapping("/listBooks")
    public Iterable<Book> getBooks() {
        return bookService.getBooks();
    }

    // define a GET method to find a book by its ID
    @GetMapping("/findBookByID/{id}")
    public Book findBookById(@PathVariable Integer id) {
        return bookService.findBookById(id);
    }

    // define a POST method to update an existing book
    @PostMapping("/updateBookByID/{id}")
    public String updateCustomer(@PathVariable Integer id, @RequestBody Book book) {
        bookService.updateBook(id, book);

        return "Updated book given with BookID: " + id;
    }
    // define a POST method to delete an existing book
    @PostMapping("/deleteBook")
    public String deleteBook(@RequestParam Integer id) {
        bookService.deleteBook(id);
        return "Delete the book successfully!";
    }
    // define a POST method to rate a book
    @PostMapping("/rateBook/{id}")
    public String rateBook(@PathVariable Integer id, @RequestParam Integer rate) {
        bookService.rateBook(id, rate);

        return "Rated book given ID: " + id.toString();
    }

    // handle GET requests to verify a user's login authentification
    @GetMapping("/login")
    public String login(@RequestParam String userName, @RequestParam String password) {
        return Boolean.toString(customerService.verifyUser(userName, password));
    }

    // handle GET requests to retrieve the admin name
    @GetMapping("/getAdminName")
    public String getAdminName() {
        return customerService.getAdminName();
    }
}
