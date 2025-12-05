package com.loan.poc.userservice.service;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.loan.poc.userservice.model.User;
import com.loan.poc.userservice.repository.UserRepository;

@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var authorities = Arrays.stream((u.getRoles() == null ? "ROLE_USER" : u.getRoles()).split(","))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPasswordHash(), authorities);
    }
}
