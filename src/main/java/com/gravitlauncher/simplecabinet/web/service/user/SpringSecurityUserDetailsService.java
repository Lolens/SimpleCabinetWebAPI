package com.gravitlauncher.simplecabinet.web.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SpringSecurityUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService service;
    @Autowired
    private com.gravitlauncher.simplecabinet.web.service.user.UserDetailsService userDetailsService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = service.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        var entity = user.get();
        return userDetailsService.makeDetails(entity);
    }
}
