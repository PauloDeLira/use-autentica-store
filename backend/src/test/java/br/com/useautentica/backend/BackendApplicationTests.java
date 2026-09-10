package br.com.useautentica.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: sobe o contexto Spring, conecta no banco e roda as migrations.
 * Requer um PostgreSQL disponível (ver docker-compose.yml).
 */
@SpringBootTest
@ActiveProfiles("test")
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }
}
