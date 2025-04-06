package com.example.bingenow.controller;

import com.example.bingenow.model.Customer;
import com.example.bingenow.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody Customer customer) {
        if (customer.getUserName() == null || customer.getUserName().isEmpty() ||
                customer.getEmail() == null || customer.getEmail().isEmpty() ||
                customer.getPassword() == null || customer.getPassword().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("All fields are required.");
        }

        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email already in use. Please use a different email or log in.");
        }

        if (!isValidPassword(customer.getPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body("Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, and one special character.");
        }
        customer.setPassword(bCryptPasswordEncoder.encode(customer.getPassword()));
        Customer savedCustomer = customerRepository.save(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<Customer> customerOptional = customerRepository.findByEmail(email);
        if (customerOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        Customer customer = customerOptional.get();
        if (bCryptPasswordEncoder.matches(password, customer.getPassword())) {
            HashMap<String, Object> response = new HashMap<>();
            response.put("message", "Authentication successful");
            response.put("userName", customer.getUserName());
            response.put("email", customer.getEmail());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$";
        return password.matches(passwordRegex);
    }
}
