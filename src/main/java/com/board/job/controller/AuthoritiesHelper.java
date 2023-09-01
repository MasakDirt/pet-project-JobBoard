package com.board.job.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public class AuthoritiesHelper {
    public static Set<String> getAuthorities(Authentication authentication) {
       return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }
}
