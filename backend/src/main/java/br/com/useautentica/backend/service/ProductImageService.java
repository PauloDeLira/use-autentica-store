package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.productimage.ProductImageResponse;
import br.com.useautentica.backend.entity.Product;
import br.com.useautentica.backend.entity.ProductImage;
import br.com.useautentica.backend.exception.BusinessException;
import br.com.useautentica.backend.exception.ResourceNotFoundException;
import br.com.useautentica.backend.repository.ProductImageRepository;
import br.com.useautentica.backend.storage.ImageStorageService;
import br.com.useautentica.backend.storage.StoredFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

@Service
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductService productService;
    private final ImageStorageService imageStorageService;

    public ProductImageService(
            ProductImageRepository productImageRepository,
            ProductService productService,
            ImageStorageService imageStorageService
    ) {
        this.productImageRepository = productImageRepository;
        this.productService = productService;
        this.imageStorageService = imageStorageService;
    }

    @Transactional(readOnly = true)
    public List<ProductImageResponse> findByProduct(UUID productId) {
        return productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProductImageResponse upload(UUID productId, MultipartFile file, String altText) {
        Product product = productService.findByIdOrThrow(productId);

        if (file.isEmpty()) {
            throw new BusinessException("IMAGE_FILE_REQUIRED", "O arquivo de imagem é obrigatório");
        }

        // O Content-Type do multipart é declarado pelo cliente e pode ser forjado;
        // a extensão real é decidida pela assinatura de bytes do próprio arquivo.
        String extension = detectExtensionByMagicBytes(file);
        if (extension == null) {
            throw new BusinessException("IMAGE_FORMAT_NOT_SUPPORTED", "Formato de imagem não suportado. Use JPEG, PNG ou WEBP");
        }

        StoredFile stored = imageStorageService.store(file, extension);
        int displayOrder = productImageRepository.countByProductId(productId);
        ProductImage image = new ProductImage(product, stored.storageKey(), stored.url(), altText, displayOrder);
        return toResponse(productImageRepository.save(image));
    }

    private String detectExtensionByMagicBytes(MultipartFile file) {
        byte[] header = new byte[12];
        int read;
        try (var in = file.getInputStream()) {
            read = in.readNBytes(header, 0, header.length);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao ler o arquivo de imagem enviado", e);
        }

        if (read >= 3 && (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF) {
            return ".jpg";
        }
        if (read >= 8
                && (header[0] & 0xFF) == 0x89 && header[1] == 'P' && header[2] == 'N' && header[3] == 'G'
                && header[4] == 0x0D && header[5] == 0x0A && header[6] == 0x1A && header[7] == 0x0A) {
            return ".png";
        }
        if (read >= 12
                && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
            return ".webp";
        }
        return null;
    }

    @Transactional
    public void delete(UUID productId, UUID imageId) {
        ProductImage image = findByProductAndIdOrThrow(productId, imageId);
        imageStorageService.delete(image.getStorageKey());
        productImageRepository.delete(image);
    }

    private ProductImage findByProductAndIdOrThrow(UUID productId, UUID imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("IMAGE_NOT_FOUND", "Imagem não encontrada"));
        if (!image.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("IMAGE_NOT_FOUND", "Imagem não encontrada");
        }
        return image;
    }

    private ProductImageResponse toResponse(ProductImage image) {
        return new ProductImageResponse(image.getId(), image.getUrl(), image.getAltText(), image.getDisplayOrder());
    }
}
