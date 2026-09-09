package com.campus.lostfound.security;

import com.campus.lostfound.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository users;

    public JwtFilter(JwtService jwtService, UserRepository users) {
        this.jwtService=jwtService; this.users=users;
    }

    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String auth=req.getHeader("Authorization");
        if(auth!=null && auth.startsWith("Bearer ")) {
            String token=auth.substring(7);
            if(jwtService.valid(token)) {
                String email=jwtService.extractEmail(token);
                users.findByEmail(email).ifPresent(u -> {
                    var authorities=List.of(new SimpleGrantedAuthority("ROLE_"+u.getRole().name()));
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(u.getEmail(),null,authorities));
                });
            }
        }
        chain.doFilter(req,res);
    }
}
