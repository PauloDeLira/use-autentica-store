package br.com.useautentica.backend.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    StoredFile store(MultipartFile file, String extension);

    void delete(String storageKey);
}
