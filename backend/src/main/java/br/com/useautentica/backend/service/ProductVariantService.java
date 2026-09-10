package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.color.ColorResponse;
import br.com.useautentica.backend.dto.productvariant.ProductVariantRequest;
import br.com.useautentica.backend.dto.productvariant.ProductVariantResponse;
import br.com.useautentica.backend.dto.productvariant.ProductVariantSummary;
import br.com.useautentica.backend.dto.size.SizeResponse;
import br.com.useautentica.backend.entity.Color;
import br.com.useautentica.backend.entity.Product;
import br.com.useautentica.backend.entity.ProductVariant;
import br.com.useautentica.backend.entity.Size;
import br.com.useautentica.backend.exception.BusinessException;
import br.com.useautentica.backend.exception.ResourceNotFoundException;
import br.com.useautentica.backend.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductService productService;
    private final SizeService sizeService;
    private final ColorService colorService;

    public ProductVariantService(
            ProductVariantRepository productVariantRepository,
            ProductService productService,
            SizeService sizeService,
            ColorService colorService
    ) {
        this.productVariantRepository = productVariantRepository;
        this.productService = productService;
        this.sizeService = sizeService;
        this.colorService = colorService;
    }

    @Transactional(readOnly = true)
    public List<ProductVariantSummary> findActiveByProductForCatalog(UUID productId) {
        return productVariantRepository.findByProductIdAndActiveTrue(productId).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductVariantResponse> findAllForInventory() {
        return productVariantRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProductVariantResponse create(UUID productId, ProductVariantRequest request) {
        Product product = productService.findByIdOrThrow(productId);
        Size size = sizeService.findActiveByIdOrThrow(request.sizeId());
        Color color = colorService.findActiveByIdOrThrow(request.colorId());

        if (productVariantRepository.existsByProductIdAndSizeIdAndColorId(productId, size.getId(), color.getId())) {
            throw new BusinessException("VARIANT_ALREADY_EXISTS", "Já existe uma variação com esse tamanho e cor para este produto");
        }

        String sku = generateSku(product, size, color);
        ProductVariant variant = new ProductVariant(product, size, color, sku, request.stockQuantity());
        return toResponse(productVariantRepository.save(variant));
    }

    @Transactional
    public ProductVariantResponse update(UUID productId, UUID variantId, ProductVariantRequest request) {
        ProductVariant variant = findByProductAndIdOrThrow(productId, variantId);
        Size size = sizeService.findActiveByIdOrThrow(request.sizeId());
        Color color = colorService.findActiveByIdOrThrow(request.colorId());

        if (productVariantRepository.existsByProductIdAndSizeIdAndColorIdAndIdNot(productId, size.getId(), color.getId(), variantId)) {
            throw new BusinessException("VARIANT_ALREADY_EXISTS", "Já existe uma variação com esse tamanho e cor para este produto");
        }

        variant.setSize(size);
        variant.setColor(color);
        variant.setStockQuantity(request.stockQuantity());
        variant.setSku(generateSku(variant.getProduct(), size, color));
        return toResponse(variant);
    }

    @Transactional
    public void delete(UUID productId, UUID variantId) {
        ProductVariant variant = findByProductAndIdOrThrow(productId, variantId);
        variant.setActive(false);
    }

    @Transactional
    public ProductVariantResponse updateStock(UUID variantId, int stockQuantity) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("VARIANT_NOT_FOUND", "Variação não encontrada"));
        variant.setStockQuantity(stockQuantity);
        return toResponse(variant);
    }

    private ProductVariant findByProductAndIdOrThrow(UUID productId, UUID variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("VARIANT_NOT_FOUND", "Variação não encontrada"));
        if (!variant.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("VARIANT_NOT_FOUND", "Variação não encontrada");
        }
        return variant;
    }

    private String generateSku(Product product, Size size, Color color) {
        String productPrefix = product.getId().toString().replace("-", "").substring(0, 6).toUpperCase();
        return productPrefix + "-" + size.getName().toUpperCase() + "-" + color.getName().toUpperCase();
    }

    private ProductVariantResponse toResponse(ProductVariant variant) {
        SizeResponse sizeResponse = new SizeResponse(
                variant.getSize().getId(), variant.getSize().getName(), variant.getSize().getDisplayOrder(), variant.getSize().isActive());
        ColorResponse colorResponse = new ColorResponse(
                variant.getColor().getId(), variant.getColor().getName(), variant.getColor().getHexCode(), variant.getColor().isActive());
        return new ProductVariantResponse(
                variant.getId(), variant.getProduct().getId(), variant.getProduct().getName(), variant.getSku(),
                sizeResponse, colorResponse, variant.getStockQuantity(), variant.isActive(), variant.isAvailable());
    }

    private ProductVariantSummary toSummary(ProductVariant variant) {
        return new ProductVariantSummary(
                variant.getId(), variant.getSize().getName(), variant.getColor().getName(),
                variant.getColor().getHexCode(), variant.getStockQuantity(), variant.isAvailable());
    }
}
