package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.productvariant.ProductVariantRequest;
import br.com.useautentica.backend.dto.productvariant.ProductVariantResponse;
import br.com.useautentica.backend.entity.Category;
import br.com.useautentica.backend.entity.Color;
import br.com.useautentica.backend.entity.Product;
import br.com.useautentica.backend.entity.ProductVariant;
import br.com.useautentica.backend.entity.Size;
import br.com.useautentica.backend.exception.BusinessException;
import br.com.useautentica.backend.exception.ResourceNotFoundException;
import br.com.useautentica.backend.repository.ProductVariantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductVariantServiceTest {

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private ProductService productService;

    @Mock
    private SizeService sizeService;

    @Mock
    private ColorService colorService;

    @InjectMocks
    private ProductVariantService productVariantService;

    private Product product(UUID productId) {
        Product product = new Product("Camiseta Oversized", null, new BigDecimal("89.90"), new Category("Camisetas", null));
        try {
            var field = Product.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(product, productId);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return product;
    }

    @Test
    void createsVariantWithDeterministicSku() {
        UUID productId = UUID.fromString("a3f2c1d4-1111-2222-3333-444455556666");
        Product product = product(productId);
        Size size = new Size("M", 2);
        Color color = new Color("Preto", "#000000");

        when(productService.findByIdOrThrow(productId)).thenReturn(product);
        when(sizeService.findActiveByIdOrThrow(any())).thenReturn(size);
        when(colorService.findActiveByIdOrThrow(any())).thenReturn(color);
        when(productVariantRepository.existsByProductIdAndSizeIdAndColorId(any(), any(), any())).thenReturn(false);
        when(productVariantRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductVariantResponse response = productVariantService.create(
                productId, new ProductVariantRequest(UUID.randomUUID(), UUID.randomUUID(), 10));

        assertThat(response.sku()).isEqualTo("A3F2C1-M-PRETO");
        assertThat(response.stockQuantity()).isEqualTo(10);
        assertThat(response.available()).isTrue();
    }

    @Test
    void rejectsCreateWhenCombinationAlreadyExists() {
        UUID productId = UUID.randomUUID();
        when(productService.findByIdOrThrow(productId)).thenReturn(product(productId));
        when(sizeService.findActiveByIdOrThrow(any())).thenReturn(new Size("M", 2));
        when(colorService.findActiveByIdOrThrow(any())).thenReturn(new Color("Preto", "#000000"));
        when(productVariantRepository.existsByProductIdAndSizeIdAndColorId(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> productVariantService.create(
                productId, new ProductVariantRequest(UUID.randomUUID(), UUID.randomUUID(), 10)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void softDeletesVariantInsteadOfRemovingRow() {
        UUID productId = UUID.randomUUID();
        UUID variantId = UUID.randomUUID();
        Product product = product(productId);
        ProductVariant variant = new ProductVariant(product, new Size("M", 2), new Color("Preto", "#000000"), "SKU-1", 5);
        when(productVariantRepository.findById(variantId)).thenReturn(Optional.of(variant));

        productVariantService.delete(productId, variantId);

        assertThat(variant.isActive()).isFalse();
    }

    @Test
    void rejectsOperationWhenVariantBelongsToAnotherProduct() {
        UUID productId = UUID.randomUUID();
        UUID otherProductId = UUID.randomUUID();
        UUID variantId = UUID.randomUUID();
        Product otherProduct = product(otherProductId);
        ProductVariant variant = new ProductVariant(otherProduct, new Size("M", 2), new Color("Preto", "#000000"), "SKU-1", 5);
        when(productVariantRepository.findById(variantId)).thenReturn(Optional.of(variant));

        assertThatThrownBy(() -> productVariantService.delete(productId, variantId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updatesStockToAbsoluteValue() {
        UUID variantId = UUID.randomUUID();
        ProductVariant variant = new ProductVariant(
                product(UUID.randomUUID()), new Size("M", 2), new Color("Preto", "#000000"), "SKU-1", 5);
        when(productVariantRepository.findById(variantId)).thenReturn(Optional.of(variant));

        ProductVariantResponse response = productVariantService.updateStock(variantId, 20);

        assertThat(response.stockQuantity()).isEqualTo(20);
    }

    @Test
    void throwsNotFoundWhenUpdatingStockOfMissingVariant() {
        UUID variantId = UUID.randomUUID();
        when(productVariantRepository.findById(variantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productVariantService.updateStock(variantId, 20))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
