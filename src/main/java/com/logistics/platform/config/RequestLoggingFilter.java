package com.logistics.platform.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            
            // Format: [200 OK] PUT /api/v1/admin/users/... (15ms)
            logger.info("[{} {}] {} {} ({}ms)", 
                status, 
                getHttpStatusText(status), 
                method, 
                uri, 
                duration
            );
        }
    }

    private String getHttpStatusText(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) return "OK";
        if (statusCode >= 400 && statusCode < 500) return "Client Error";
        if (statusCode >= 500) return "Server Error";
        return "Status";
    }
}
