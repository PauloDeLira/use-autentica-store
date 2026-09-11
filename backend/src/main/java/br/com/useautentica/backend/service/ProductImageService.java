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

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductImageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

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
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BusinessException("IMAGE_FORMAT_NOT_SUPPORTED", "Formato de imagem não suportado. Use JPEG, PNG ou WEBP");
        }

        StoredFile stored = imageStorageService.store(file);
        int displayOrder = productImageRepository.countByProductId(productId);
        ProductImage image = new ProductImage(product, stored.storageKey(), stored.url(), altText, displayOrder);
        return toResponse(productImageRepository.save(image));
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
