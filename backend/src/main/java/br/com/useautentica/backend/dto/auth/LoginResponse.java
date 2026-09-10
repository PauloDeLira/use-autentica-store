package br.com.useautentica.backend.dto.auth;

public record LoginResponse(
        String token,
        String tokenType,
        String name,
        String email,
        String role
) {
    public LoginResponse(String token, String name, String email, String role) {
        this(token, "Bearer", name, email, role);
    }
}
