package com.codesio.magnum_opus.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@SuppressWarnings("PMD.AtLeastOneConstructor")
class RequestCorrelationFilter extends OncePerRequestFilter {

  private static final String REQUEST_ID_HEADER = "X-Request-Id";
  private static final String MDC_KEY = "request.id";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    final String requestId = resolveRequestId(request);
    response.setHeader(REQUEST_ID_HEADER, requestId);
    MDC.put(MDC_KEY, requestId);

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_KEY);
    }
  }

  private static String resolveRequestId(HttpServletRequest request) {
    final String requestId = request.getHeader(REQUEST_ID_HEADER);

    if (requestId == null || requestId.isBlank()) {
      return UUID.randomUUID().toString();
    }

    return requestId;
  }
}
