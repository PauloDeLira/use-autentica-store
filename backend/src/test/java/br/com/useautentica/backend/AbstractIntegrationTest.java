package br.com.useautentica.backend;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base para os testes de integração: sobe um Postgres real isolado via
 * Testcontainers, em vez de depender do banco de dev compartilhado. O
 * container é único por execução da suíte inteira (padrão "singleton
 * container" recomendado pelo Spring Boot) — iniciado uma vez aqui e
 * reaproveitado por todas as subclasses, sem precisar reiniciar a cada
 * classe de teste.
 */
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        POSTGRES.start();
    }
}
