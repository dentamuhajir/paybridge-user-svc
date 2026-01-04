package com.paybridge.user.service.security;

import com.paybridge.user.service.entity.User;
import com.paybridge.user.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var authorities = List.of(
                new SimpleGrantedAuthority(user.getRole().getName())
        );

        return new AppUserDetails(
                user.getId().toString(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
//        return org.springframework.security.core.userdetails.User
//                .builder()
//                .username(user.getEmail())
//                .password(user.getPassword())
//                .roles(user.getRole().getName().replace("ROLE_", ""))
//                .build();
    }

}
