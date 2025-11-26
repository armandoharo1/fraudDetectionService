package com.armando.frauddetection.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1) // se ejecuta temprano en la cadena de filtros
public class RequestCorrelationFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String correlationId = request.getHeader(CORRELATION_ID_HEADER);

        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }

        // Lo guardamos en MDC para que aparezca en los logs
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);

        // También lo devolvemos en la respuesta
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // limpiamos el contexto para no “ensuciar” otros requests
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }
}
