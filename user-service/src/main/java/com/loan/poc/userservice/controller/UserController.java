package com.loan.poc.userservice.controller;

import com.loan.poc.userservice.dto.UserValidationResponse;
import com.loan.poc.userservice.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/validate/{username}")
    public ResponseEntity<UserValidationResponse> validateUser(@PathVariable String username) {
        return userService.validateUser(username);
    }

}
