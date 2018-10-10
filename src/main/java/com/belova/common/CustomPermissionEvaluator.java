package com.belova.common;

import com.belova.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Override
    public boolean hasPermission(
            Authentication auth, Object targetDomainObject, Object permission) {
        if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)) {
            return false;
        }
        if(((User)targetDomainObject).getLogin()==null) {
            return false;
        }
        //String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();
        String targetLogin = ((User)targetDomainObject).getLogin();

        return hasPrivilege(auth, targetLogin, permission.toString());
    }

    @Override
    public boolean hasPermission(
            Authentication auth, Serializable targetId, String targetType, Object permission) {
        if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }
        return hasPrivilege(auth, targetType.toUpperCase(),
                permission.toString().toUpperCase());
    }


    private boolean hasPrivilege(Authentication auth, String targetLogin, String permission) {
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(targetLogin);
        for (GrantedAuthority grantedAuth : userDetails.getAuthorities()) {
            if (grantedAuth.getAuthority().contains(permission)) {
                return true;
            }
        }
        return false;
    }
}
