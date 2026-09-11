package br.com.useautentica.backend.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class LocalImageStorageService implements ImageStorageService {

    private final Path basePath;

    public LocalImageStorageService(@Value("${app.storage.local.base-path}") String basePath) {
        this.basePath = Path.of(basePath);
    }

    @Override
    public StoredFile store(MultipartFile file, String extension) {
        try {
            Files.createDirectories(basePath);
            String storageKey = UUID.randomUUID() + extension;
            file.transferTo(basePath.resolve(storageKey));
            return new StoredFile(storageKey, "/uploads/" + storageKey);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao salvar o arquivo de imagem", e);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(basePath.resolve(storageKey));
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao remover o arquivo de imagem", e);
        }
    }
}
