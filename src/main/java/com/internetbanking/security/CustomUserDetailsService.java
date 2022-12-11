package com.internetbanking.security;

import com.internetbanking.entity.Role;
import com.internetbanking.entity.User;
import com.internetbanking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Email is invalid!"));
        return UserDetailsDto.builder()
                .user(user)
                .userId(user.getId())
                .email(user.getEmail())
                .accountId(user.getAccount().getId())
                .role(user.getRole().getCode())
                .build();
//        return org.springframework.security.core.userdetails.User
//                .withUsername(user.getEmail())
//                .password(user.getPassword())
//                .authorities(new SimpleGrantedAuthority(user.getRole().getCode()))
//                .build();
    }

}
