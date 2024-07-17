package com.backend.project.config;

import com.backend.project.enums.Messages;
import com.backend.project.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final JwtUtil util;

    @Autowired
    TokenFilter(JwtUtil uti){
        this.util = uti;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
        } else {
            final String authHeader = request.getHeader("Authorization");
            String jwt = null;
            if(authHeader != null && authHeader.startsWith("Bearer ")){
                jwt = authHeader.split(" ")[1];
            }
            String tokenStatus = util.verifyJwts(jwt);
            if(tokenStatus.equals(Messages.TKN_VALD.toString())){
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("text/plain");
                response.getWriter().write(tokenStatus);
            }
//                throw new AccessDeniedException(Messages.ACCES_DND.toString());
        }
    }
}
