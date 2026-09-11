package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.category.CategoryResponse;
import br.com.useautentica.backend.dto.product.ProductRequest;
import br.com.useautentica.backend.dto.product.ProductResponse;
import br.com.useautentica.backend.dto.product.ProductSummaryResponse;
import br.com.useautentica.backend.dto.productimage.ProductImageResponse;
import br.com.useautentica.backend.entity.Category;
import br.com.useautentica.backend.entity.Product;
import br.com.useautentica.backend.entity.ProductImage;
import br.com.useautentica.backend.exception.ResourceNotFoundException;
import br.com.useautentica.backend.repository.ProductImageRepository;
import br.com.useautentica.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductImageRepository productImageRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryService categoryService,
            ProductImageRepository productImageRepository
    ) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productImageRepository = productImageRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> findActiveForCatalog() {
        return productRepository.findByActiveTrue().stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findActiveByIdForCatalog(UUID id) {
        Product product = findByIdOrThrow(id);
        if (!product.isActive()) {
            throw new ResourceNotFoundException("PRODUCT_NOT_FOUND", "Produto não encontrado");
        }
        return toResponse(product);
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
        productRepository.delete(findByIdOrThrow(id));
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

    private ProductSummaryResponse toSummary(Product product) {
        return new ProductSummaryResponse(
                product.getId(), product.getName(), product.getPrice(), product.isActive(), product.getCategory().getName());
    }
}
