package com.example.demo.security;

import com.example.demo.model.persistence.repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepo;

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    public UserDetailsServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername (String username) throws UsernameNotFoundException {

        com.example.demo.model.persistence.User ecomUser = userRepo.findByUsername(username);
        if (ecomUser == null) {
            log.error("Error : Username : " + username + " : Does not exist");
            throw new UsernameNotFoundException(username);
            //return new User("", "", emptyList());
        }
        return new User(ecomUser.getUsername(), ecomUser.getPassword(), emptyList());
    }
}
