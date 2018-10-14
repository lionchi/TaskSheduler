package com.belova.common;

import com.belova.entity.Privilege;
import com.belova.entity.User;
import com.belova.entity.UserRole;
import com.belova.entity.repository.UserRepository;
import com.belova.entity.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {

        com.belova.entity.User user = userRepository.findByLogin(login);
        if (user == null) {
            return new org.springframework.security.core.userdetails.User(
                    " ", " ", true, true, true, true,
                    getAuthorities(Arrays.asList(
                            roleRepository.findByRolename("USER"))));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(), user.getPassword(), user.getEnabled() == 1, true, true,
                true, getAuthorities(Collections.singletonList(user.getUserRole())));
    }

    public User loadUserById(Serializable id, String targetType) {
        User findUser = null;
        if (targetType.equals("User")) {
            findUser = userRepository.findOne((Long) id);
        }
        return findUser;
    }


    private Collection<? extends GrantedAuthority> getAuthorities(
            Collection<UserRole> roles) {

        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(Collection<UserRole> roles) {

        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();
        for (UserRole role : roles) {
            collection.addAll(role.getPrivileges());
        }
        for (Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }
}
