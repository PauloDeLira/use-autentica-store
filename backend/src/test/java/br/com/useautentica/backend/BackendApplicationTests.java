package br.com.useautentica.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Teste de smoke: garante que o contexto Spring sobe corretamente,
 * incluindo a conexão com o banco e a execução das migrations Flyway.
 *
 * Requer um PostgreSQL disponível (ver docker-compose.yml). Testes de
 * integração mais completos com Testcontainers serão adicionados a
 * partir da Sprint 04.
 */
@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }
}
