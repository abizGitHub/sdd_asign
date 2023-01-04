package com.abiz.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service("userService")
public class JpaUserDetailService implements UserDetailsService {

    @Autowired
    UserDetailRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserDetail> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) {
            return null;
        }
        UserDetail user = optional.get();
        UserDetails build = User.builder().username(user.getUsername())
                .password(user.getPassword())
                .accountExpired(false)
                .disabled(false)
                .accountLocked(false)
                .roles(user.getRoles().split(","))
                .build();
        return build;
    }
}
