package com.loan.poc.userservice.controller;


import com.loan.poc.userservice.dto.*;
import com.loan.poc.userservice.model.User;
import com.loan.poc.userservice.repository.UserRepository;
import com.loan.poc.userservice.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserRepository repo;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private AuthenticationManager authManager;
    @Autowired private JWTUtil jwtUtil;

    @GetMapping("get")
    public String getMethodName() {
        return "Hello Auth";
    }
    

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (repo.existsByUsername(req.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setRoles("ROLE_USER");
        repo.save(u);
        return ResponseEntity.ok(new RegisterResponse("Registered", u.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        final var userDetails = repo.findByUsername(req.getUsername()).orElseThrow();
        final var token = jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(userDetails.getUsername(), userDetails.getPasswordHash(), java.util.List.of()));
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
