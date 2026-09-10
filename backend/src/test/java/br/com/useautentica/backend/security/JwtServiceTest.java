package br.com.useautentica.backend.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            "dGVzdC1vbmx5LXNlY3JldC1uYW8tdXNhci1lbS1wcm9kdWNhbw==", 60);

    @Test
    void generatesTokenAndExtractsClaims() {
        String token = jwtService.generateToken("admin@useautentica.com", "ADMIN");

        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractEmail(token)).isEqualTo("admin@useautentica.com");
        assertThat(jwtService.extractRole(token)).isEqualTo("ADMIN");
    }

    @Test
    void rejectsTamperedToken() {
        String token = jwtService.generateToken("admin@useautentica.com", "ADMIN");
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThat(jwtService.isValid(tampered)).isFalse();
    }

    @Test
    void rejectsTokenSignedWithDifferentSecret() {
        JwtService otherService = new JwtService(
                "b3V0cmEtY2hhdmUtZGlmZXJlbnRlLXBhcmEtbyt0ZXN0ZQ==", 60);
        String token = otherService.generateToken("admin@useautentica.com", "ADMIN");

        assertThat(jwtService.isValid(token)).isFalse();
    }

    @Test
    void rejectsExpiredToken() {
        JwtService expiringService = new JwtService(
                "dGVzdC1vbmx5LXNlY3JldC1uYW8tdXNhci1lbS1wcm9kdWNhbw==", -1);
        String token = expiringService.generateToken("admin@useautentica.com", "ADMIN");

        assertThatThrownBy(() -> expiringService.extractEmail(token))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
        assertThat(expiringService.isValid(token)).isFalse();
    }
}
