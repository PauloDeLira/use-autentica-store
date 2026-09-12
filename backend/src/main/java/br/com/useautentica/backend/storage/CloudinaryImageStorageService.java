package br.com.useautentica.backend.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.UUID;

/**
 * Ativo em produção: disco local não é persistente na maioria dos provedores
 * free tier, então as imagens vão para o Cloudinary. O storageKey guardado é
 * o public_id do Cloudinary, necessário pra remover o arquivo depois.
 */
@Service
@Profile("prod")
public class CloudinaryImageStorageService implements ImageStorageService {

    private static final String FOLDER = "use-autentica/products";

    private final Cloudinary cloudinary;

    public CloudinaryImageStorageService(
            @Value("${app.storage.cloudinary.cloud-name}") String cloudName,
            @Value("${app.storage.cloudinary.api-key}") String apiKey,
            @Value("${app.storage.cloudinary.api-secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    @Override
    public StoredFile store(MultipartFile file, String extension) {
        try {
            String publicId = FOLDER + "/" + UUID.randomUUID();
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId,
                    "overwrite", false
            ));
            return new StoredFile((String) result.get("public_id"), (String) result.get("secure_url"));
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao enviar a imagem para o Cloudinary", e);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            cloudinary.uploader().destroy(storageKey, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao remover a imagem do Cloudinary", e);
        }
    }
}
