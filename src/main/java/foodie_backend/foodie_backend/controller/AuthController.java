package foodie_backend.foodie_backend.controller;

import foodie_backend.foodie_backend.dto.AuthResponse;
import foodie_backend.foodie_backend.dto.LoginRequest;
import foodie_backend.foodie_backend.dto.SignupRequest;
import foodie_backend.foodie_backend.model.Person;
import foodie_backend.foodie_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public AuthResponse signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestAttribute("currentUser") Person user) {
        authService.logout(user);
        return Map.of("message", "Logged out");
    }
}
