package foodie_backend.foodie_backend.web;

import foodie_backend.foodie_backend.dto.SignupRequest;
import foodie_backend.foodie_backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Test
    void adminApiWithoutATokenReturns401() throws Exception {
        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void adminApiWithACustomerTokenReturns403() throws Exception {
        String token = signupAndGetToken("customer.403@foodies.com");

        mockMvc.perform(get("/api/admin/orders").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void placeOrderWithoutLoginStillReachesTheApi() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "",
                                  "phone": "123",
                                  "address": "",
                                  "paymentMethod": "",
                                  "items": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void invalidOrderBodyReturns400WithAnErrorMessage() throws Exception {
        String token = signupAndGetToken("customer.400@foodies.com");
        String body = """
                {
                  "customerName": "",
                  "phone": "123",
                  "address": "",
                  "paymentMethod": "",
                  "items": []
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    private String signupAndGetToken(String email) {
        SignupRequest request = new SignupRequest();
        request.setName("Test Customer");
        request.setEmail(email);
        request.setPassword("secret123");
        return authService.signup(request).getToken();
    }
}
