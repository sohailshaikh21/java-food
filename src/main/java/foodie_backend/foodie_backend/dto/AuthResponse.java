package foodie_backend.foodie_backend.dto;

public class AuthResponse {

    private final String token;
    private final UserInfo user;

    public AuthResponse(String token, String name, String role) {
        this.token = token;
        this.user = new UserInfo(name, role);
    }

    public String getToken() {
        return token;
    }

    public UserInfo getUser() {
        return user;
    }

    public static class UserInfo {

        private final String name;
        private final String role;

        public UserInfo(String name, String role) {
            this.name = name;
            this.role = role;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }
    }
}
