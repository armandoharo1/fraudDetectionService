package com.armando.frauddetection.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class RequestCorrelationFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Obtener correlationId del header o generarlo
            String correlationId = request.getHeader(CORRELATION_ID_HEADER);
            if (!StringUtils.hasText(correlationId)) {
                correlationId = UUID.randomUUID().toString();
            }

            // 2. Guardar en MDC (para logs) y devolver en la respuesta
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            response.setHeader(CORRELATION_ID_HEADER, correlationId);

            // 3. Log básico de entrada
            log.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

            // 4. Continuar la cadena de filtros
            filterChain.doFilter(request, response);

        } finally {
            // 5. Limpiar el MDC
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }
}
