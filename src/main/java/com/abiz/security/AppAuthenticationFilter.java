package com.abiz.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@Component
public class AppAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JpaUserDetailService userDetailService;

    private PasswordEncoder passwordEncoder;

    public AppAuthenticationFilter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
        String username = null;
        String password = null;
        if (auth != null) {
            String cred = decodeBase64(auth);
            if (cred !=null && cred.contains("|")) {
                username = cred.split("[|]")[0];
                password = cred.split("[|]")[1];
            }
        }
        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (passwordEncoder.matches(password, userDetails.getPassword())) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                logger.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(req, res);
    }

    private String decodeBase64(String base64Token) {
        try {
            return new String(Base64.getDecoder().decode(base64Token.getBytes()));
        } catch (IllegalArgumentException ex) {
           return null;
        }
    }
}
