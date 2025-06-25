package uz.yeysam.Yeysam.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", System.currentTimeMillis());
        data.put("status", HttpStatus.UNAUTHORIZED.value());
        data.put("error", "Unauthorized");
        data.put("message", "Authentication failed or you do not have access to this resource");
        data.put("path", request.getRequestURI());

        objectMapper.writeValue(response.getWriter(), data);
    }
}
