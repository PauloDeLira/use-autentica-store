package br.com.useautentica.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: sobe o contexto Spring, conecta no banco e roda as migrations.
 * Requer um PostgreSQL disponível (ver docker-compose.yml).
 */
@SpringBootTest
class BackendApplicationTests extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
    }
}
