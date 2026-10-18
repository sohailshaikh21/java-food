package foodie_backend.foodie_backend.service;

import foodie_backend.foodie_backend.dto.AuthResponse;
import foodie_backend.foodie_backend.dto.LoginRequest;
import foodie_backend.foodie_backend.dto.SignupRequest;
import foodie_backend.foodie_backend.exception.ConflictException;
import foodie_backend.foodie_backend.exception.ForbiddenException;
import foodie_backend.foodie_backend.exception.UnauthorizedException;
import foodie_backend.foodie_backend.model.Customer;
import foodie_backend.foodie_backend.model.Person;
import foodie_backend.foodie_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class AuthService {

    private static final int TOKEN_VALIDITY_DAYS = 7;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse signup(SignupRequest request) {
        String email = normalise(request.getEmail());
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("An account with this email already exists");
        }

        Customer customer = new Customer(request.getName().trim(), null, email);
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.insert(customer);

        return startSession(customer);
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalise(request.getEmail());
        Person person = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), person.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return startSession(person);
    }

    public void logout(Person person) {
        userRepository.clearToken(person.getId());
    }

    public Person authenticate(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Please log in to continue");
        }
        return userRepository.findByValidToken(token)
                .orElseThrow(() -> new UnauthorizedException("Your session has expired, please log in again"));
    }

    public void requireAdmin(Person person) {
        if (!"ADMIN".equals(person.getRole())) {
            throw new ForbiddenException("This area is for admins only");
        }
    }

    private AuthResponse startSession(Person person) {
        String token = generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(TOKEN_VALIDITY_DAYS);
        userRepository.updateToken(person.getId(), token, expiresAt);
        person.setAuthToken(token);
        person.setTokenExpiresAt(expiresAt);
        return new AuthResponse(token, person.getName(), person.getRole());
    }

    private String generateToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String normalise(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
