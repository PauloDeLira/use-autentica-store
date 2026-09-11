package br.com.useautentica.backend.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    StoredFile store(MultipartFile file);

    void delete(String storageKey);
}
