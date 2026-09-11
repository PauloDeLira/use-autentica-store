package br.com.useautentica.backend.config;

import br.com.useautentica.backend.entity.Size;
import br.com.useautentica.backend.repository.SizeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Cadastra os tamanhos básicos (PP a G) no primeiro boot, para não exigir
 * esse cadastro manual repetido a cada produto novo. GG/XG e outros ficam
 * a critério da administradora, via tela de variações.
 */
@Component
public class SizeSeeder implements CommandLineRunner {

    private static final List<String> DEFAULT_SIZES = List.of("PP", "P", "M", "G");

    private final SizeRepository sizeRepository;

    public SizeSeeder(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    @Override
    public void run(String... args) {
        for (int i = 0; i < DEFAULT_SIZES.size(); i++) {
            String name = DEFAULT_SIZES.get(i);
            if (!sizeRepository.existsByName(name)) {
                sizeRepository.save(new Size(name, i));
            }
        }
    }
}
