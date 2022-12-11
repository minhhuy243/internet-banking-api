package com.internetbanking.service;

import com.internetbanking.security.UserDetailsDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsDto principal;
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        } else {
            principal = (UserDetailsDto) authentication.getPrincipal();
            return principal.getUserId();
        }
    }

    public String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsDto principal;
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        } else {
            principal = (UserDetailsDto) authentication.getPrincipal();
            return principal.getEmail();
        }
    }

    public Long getAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsDto principal;
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        } else {
            principal = (UserDetailsDto) authentication.getPrincipal();
            return principal.getAccountId();
        }
    }

    public String getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsDto principal;
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        } else {
            principal = (UserDetailsDto) authentication.getPrincipal();
            return principal.getRole();
        }
    }
}
