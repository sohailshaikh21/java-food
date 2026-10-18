package foodie_backend.foodie_backend.config;

import foodie_backend.foodie_backend.model.Person;
import foodie_backend.foodie_backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String CURRENT_USER = "currentUser";

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (isPublicOrdersView(request)) {
            return true;
        }

        String token = readToken(request);
        if (isGuestOrderPlacement(request) && (token == null || token.isBlank())) {
            return true;
        }

        Person person = authService.authenticate(token);

        if (request.getRequestURI().startsWith("/api/admin")) {
            authService.requireAdmin(person);
        }

        request.setAttribute(CURRENT_USER, person);
        return true;
    }

    private boolean isPublicOrdersView(HttpServletRequest request) {
        return "GET".equals(request.getMethod()) && "/api/orders".equals(request.getRequestURI());
    }

    private boolean isGuestOrderPlacement(HttpServletRequest request) {
        return "POST".equals(request.getMethod()) && "/api/orders".equals(request.getRequestURI());
    }

    private String readToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7).trim();
        }
        return null;
    }
}
