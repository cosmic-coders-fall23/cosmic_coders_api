package org.cosmiccoders.api.security;

import jakarta.transaction.Transactional;
import org.cosmiccoders.api.dao.UserWithRoles;
import org.cosmiccoders.api.model.Role;
import org.cosmiccoders.api.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceCustom implements UserDetailsService {
    private final UserEntityRepository userRepository;

    @Autowired
    public UserDetailsServiceCustom(UserEntityRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserWithRoles user = userRepository.findDtoWithRolesByEmail(email);

        if(user != null) {
            //if user exists

            //hashset populated by all authorities granted by roles, which are currently only two hierarchical roles (user and admin)
            Set<GrantedAuthority> setAuths = new HashSet<>();
            List<Role> roles = user.getRoles();
            for (Role role : roles) {
                setAuths.add(new SimpleGrantedAuthority(role.getName()));
            }
            List<GrantedAuthority> authorities = new ArrayList<>(setAuths);

            //returns userdetails
            return new User(user.getUsername(), user.getPassword(), authorities);
        }
        else throw new UsernameNotFoundException("Email or password is invalid.");
    }
}
