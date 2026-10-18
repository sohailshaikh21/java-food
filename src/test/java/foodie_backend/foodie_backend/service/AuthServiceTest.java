package foodie_backend.foodie_backend.service;

import foodie_backend.foodie_backend.dto.AuthResponse;
import foodie_backend.foodie_backend.dto.LoginRequest;
import foodie_backend.foodie_backend.dto.SignupRequest;
import foodie_backend.foodie_backend.exception.UnauthorizedException;
import foodie_backend.foodie_backend.model.Person;
import foodie_backend.foodie_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void signupCreatesACustomerAndReturnsAToken() {
        AuthResponse response = authService.signup(signup("signup@foodies.com"));

        assertNotNull(response.getToken());
        assertEquals("CUSTOMER", response.getUser().getRole());
        assertEquals("Test Customer", response.getUser().getName());
    }

    @Test
    void passwordIsStoredAsAHashAndNeverAsPlainText() {
        authService.signup(signup("hash@foodies.com"));

        Person saved = userRepository.findByEmail("hash@foodies.com").orElseThrow();
        assertNotEquals("secret123", saved.getPasswordHash());
    }

    @Test
    void loginWithTheRightPasswordReturnsAToken() {
        authService.signup(signup("login@foodies.com"));

        AuthResponse response = authService.login(login("login@foodies.com", "secret123"));

        assertNotNull(response.getToken());
        assertEquals("CUSTOMER", response.getUser().getRole());
    }

    @Test
    void wrongPasswordAndWrongEmailGiveTheSameMessage() {
        authService.signup(signup("same.message@foodies.com"));

        UnauthorizedException wrongPassword = assertThrows(UnauthorizedException.class,
                () -> authService.login(login("same.message@foodies.com", "wrong-password")));
        UnauthorizedException wrongEmail = assertThrows(UnauthorizedException.class,
                () -> authService.login(login("nobody@foodies.com", "secret123")));

        assertEquals(wrongEmail.getMessage(), wrongPassword.getMessage());
        assertEquals("Invalid email or password", wrongPassword.getMessage());
    }

    private SignupRequest signup(String email) {
        SignupRequest request = new SignupRequest();
        request.setName("Test Customer");
        request.setEmail(email);
        request.setPassword("secret123");
        return request;
    }

    private LoginRequest login(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }
}
