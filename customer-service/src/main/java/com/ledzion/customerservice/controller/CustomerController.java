package com.ledzion.customerservice.controller;

import com.ledzion.customerservice.model.BookingParameters;
import com.ledzion.customerservice.model.Customer;
import com.ledzion.customerservice.service.CustomerService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final String BOOKING_ADDED = "Bicycle booking added.";
    private static final String CUSTOMER_NOT_FOUND = "Customer not found.";
    private static final String ERROR_WHILE_ADDING_BOOKING = "Error while adding bicycle booking. Provided data incorrect.";
    private static final String ERROR_WHILE_ADDING_CUSTOMER = "Error while adding customer. Provided data incorrect.";
    private static final String SERVICE_UNAVAILABLE_ERROR_MESSAGE =
            "No Response From Customer Service at this moment. " + " Service will be back shortly.";
    private static final String BOOKING_DETAILS_MISSING = "Booking details missing.";
    private static final Object CUSTOMER_ADDED = "Customer added.";
    private final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @HystrixCommand(fallbackMethod = "getCustomerByIdFallback")
    @GetMapping(value = "/{id}")
    public ResponseEntity getCustomerById(@PathVariable("id") String id) {
        LOGGER.debug("Getting customer with id {}.", id);
        // oneline -> use 3 parameters startment
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.isPresent()
                ? ResponseEntity.status(HttpStatus.OK).body(customer.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(CUSTOMER_NOT_FOUND);
    }

    @HystrixCommand(fallbackMethod = "getAllCustomersFallback")
    @GetMapping
    public ResponseEntity getAllCustomers() {
        LOGGER.debug("Getting all customers.");
        List<Customer> customers = customerService.getAllCustomers();
        // optionally: xception can be thrown by repository and parsed into 404 by exception handler
        return customers.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(CUSTOMER_NOT_FOUND)
                : ResponseEntity.status(HttpStatus.OK).body(customers);
    }

    @HystrixCommand(fallbackMethod = "addBookingFallback")
    @PutMapping("/booking")
    public ResponseEntity addBooking(@RequestBody @Valid BookingParameters bookingParameters) {
        LOGGER.debug("Adding bicycle booking with start date {} and end date {} for customer with Id {}.",
                bookingParameters.getStartDate(),
                bookingParameters.getEndDate(), bookingParameters.getUserId());
        return customerService.addBooking(bookingParameters)
                ? ResponseEntity.status(HttpStatus.OK).body(BOOKING_ADDED)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ERROR_WHILE_ADDING_BOOKING);
    }

    @HystrixCommand(fallbackMethod = "addCustomerFallback")
    @PostMapping
    public ResponseEntity addCustomer(@RequestBody @Valid Customer customer) {
        LOGGER.debug("Adding customer: " + customer.toString() + ".");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BOOKING_DETAILS_MISSING);
        }
        return customerService.addCustomer(customer)
                ? ResponseEntity.status(HttpStatus.OK).body(CUSTOMER_ADDED)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ERROR_WHILE_ADDING_CUSTOMER);
    }

    @SuppressWarnings("unused")
    public ResponseEntity getCustomerByIdFallback(String id) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(SERVICE_UNAVAILABLE_ERROR_MESSAGE);
    }

    @SuppressWarnings("unused")
    public ResponseEntity getAllCustomersFallback() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(SERVICE_UNAVAILABLE_ERROR_MESSAGE);
    }

    @SuppressWarnings("unused")
    public ResponseEntity addBookingFallback(BookingParameters bookingParameters) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(SERVICE_UNAVAILABLE_ERROR_MESSAGE);
    }

    @SuppressWarnings("unused")
    public ResponseEntity addCustomerFallback(Customer customer) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(SERVICE_UNAVAILABLE_ERROR_MESSAGE);
    }
}
