package com.loan.poc.userservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.loan.poc.userservice.dto.UserValidationResponse;
import com.loan.poc.userservice.model.User;
import com.loan.poc.userservice.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public ResponseEntity<?> getUserById(Long id) {

        Optional<User> u = repo.findById(id);
        return u.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<UserValidationResponse> validateUser(String username) {
        Optional<User> u = repo.findByUsername(username);
        if (u.isPresent()) {
            User user = u.get();
            UserValidationResponse response = new UserValidationResponse();
            response.setUserId(user.getId());
            response.setValid(true);
            return ResponseEntity.ok(response);
        } else {
            UserValidationResponse response = new UserValidationResponse();
            response.setValid(false);
            return ResponseEntity.ok(response);
        }

    }

}
