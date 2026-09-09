package br.com.useautentica.backend.controller.publicapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Endpoint simples para validar que a API está no ar.
 * Útil na Sprint 02 para confirmar que o backend subiu corretamente e está
 * conectado ao banco (se a app subir, o Flyway já validou a conexão).
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString()
        );
    }
}
