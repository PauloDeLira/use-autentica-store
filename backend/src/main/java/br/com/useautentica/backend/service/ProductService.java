package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.category.CategoryResponse;
import br.com.useautentica.backend.dto.product.ProductRequest;
import br.com.useautentica.backend.dto.product.ProductResponse;
import br.com.useautentica.backend.dto.product.ProductSummaryResponse;
import br.com.useautentica.backend.dto.productimage.ProductImageResponse;
import br.com.useautentica.backend.entity.Category;
import br.com.useautentica.backend.entity.Product;
import br.com.useautentica.backend.entity.ProductImage;
import br.com.useautentica.backend.entity.ProductVariant;
import br.com.useautentica.backend.exception.BusinessException;
import br.com.useautentica.backend.exception.ResourceNotFoundException;
import br.com.useautentica.backend.repository.ProductImageRepository;
import br.com.useautentica.backend.repository.ProductRepository;
import br.com.useautentica.backend.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryService categoryService,
            ProductImageRepository productImageRepository,
            ProductVariantRepository productVariantRepository
    ) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productImageRepository = productImageRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> findActiveForCatalog(UUID categoryId, UUID sizeId, UUID colorId) {
        return toSummaries(productRepository.findActiveForCatalog(categoryId, sizeId, colorId));
    }

    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> findAllForAdmin() {
        return toSummaries(productRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ProductResponse findActiveByIdForCatalog(UUID id) {
        Product product = findByIdOrThrow(id);
        if (!product.isActive()) {
            throw new ResourceNotFoundException("PRODUCT_NOT_FOUND", "Produto não encontrado");
        }
        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse findByIdForAdmin(UUID id) {
        return toResponse(findByIdOrThrow(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Category category = categoryService.findActiveByIdOrThrow(request.categoryId());
        Product product = new Product(request.name(), request.description(), request.price(), category);
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = findByIdOrThrow(id);
        Category category = categoryService.findActiveByIdOrThrow(request.categoryId());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(category);
        return toResponse(product);
    }

    @Transactional
    public ProductResponse setActive(UUID id, boolean active) {
        Product product = findByIdOrThrow(id);
        product.setActive(active);
        return toResponse(product);
    }

    @Transactional
    public void delete(UUID id) {
        Product product = findByIdOrThrow(id);
        if (productVariantRepository.existsByProductId(id)) {
            throw new BusinessException(
                    "PRODUCT_HAS_VARIANTS",
                    "Produto possui variações cadastradas e não pode ser excluído. Desative o produto em vez de excluí-lo.");
        }
        productRepository.delete(product);
    }

    public Product findByIdOrThrow(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PRODUCT_NOT_FOUND", "Produto não encontrado"));
    }

    private ProductResponse toResponse(Product product) {
        Category category = product.getCategory();
        CategoryResponse categoryResponse = new CategoryResponse(
                category.getId(), category.getName(), category.getDescription(), category.isActive());
        List<ProductImageResponse> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId()).stream()
                .map(this::toImageResponse)
                .toList();
        return new ProductResponse(
                product.getId(), product.getName(), product.getDescription(), product.getPrice(),
                product.isActive(), categoryResponse, images, product.getCreatedAt(), product.getUpdatedAt());
    }

    private ProductImageResponse toImageResponse(ProductImage image) {
        return new ProductImageResponse(image.getId(), image.getUrl(), image.getAltText(), image.getDisplayOrder());
    }

    private ProductSummaryResponse toSummary(Product product, String coverImageUrl, boolean available) {
        return new ProductSummaryResponse(
                product.getId(), product.getName(), product.getPrice(), product.isActive(),
                product.getCategory().getName(), coverImageUrl, available);
    }

    private List<ProductSummaryResponse> toSummaries(List<Product> products) {
        if (products.isEmpty()) {
            return List.of();
        }

        List<UUID> productIds = products.stream().map(Product::getId).toList();

        Map<UUID, String> coverImageByProductId = productImageRepository
                .findByProductIdInOrderByDisplayOrderAsc(productIds).stream()
                .collect(Collectors.toMap(
                        image -> image.getProduct().getId(),
                        ProductImage::getUrl,
                        (first, second) -> first));

        Map<UUID, Boolean> availabilityByProductId = productVariantRepository
                .findByProductIdInAndActiveTrue(productIds).stream()
                .collect(Collectors.groupingBy(
                        variant -> variant.getProduct().getId(),
                        Collectors.reducing(false, ProductVariant::isAvailable, Boolean::logicalOr)));

        return products.stream()
                .map(product -> toSummary(
                        product,
                        coverImageByProductId.get(product.getId()),
                        availabilityByProductId.getOrDefault(product.getId(), false)))
                .toList();
    }
}
