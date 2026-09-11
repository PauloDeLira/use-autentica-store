package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.product.ProductRequest;
import br.com.useautentica.backend.dto.product.ProductResponse;
import br.com.useautentica.backend.entity.Category;
import br.com.useautentica.backend.entity.Product;
import br.com.useautentica.backend.exception.BusinessException;
import br.com.useautentica.backend.exception.ResourceNotFoundException;
import br.com.useautentica.backend.repository.ProductImageRepository;
import br.com.useautentica.backend.repository.ProductRepository;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createsProductWhenCategoryIsActive() {
        UUID categoryId = UUID.randomUUID();
        Category category = new Category("Camisetas", null);
        when(categoryService.findActiveByIdOrThrow(categoryId)).thenReturn(category);
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.create(
                new ProductRequest("Camiseta Oversized", null, new BigDecimal("89.90"), categoryId));

        assertThat(response.name()).isEqualTo("Camiseta Oversized");
        assertThat(response.active()).isTrue();
    }

    @Test
    void rejectsCreateWhenCategoryIsInactive() {
        UUID categoryId = UUID.randomUUID();
        when(categoryService.findActiveByIdOrThrow(categoryId))
                .thenThrow(new BusinessException("CATEGORY_INACTIVE", "A categoria informada está inativa"));

        assertThatThrownBy(() -> productService.create(
                new ProductRequest("Camiseta Oversized", null, new BigDecimal("89.90"), categoryId)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void hidesInactiveProductFromCatalog() {
        UUID id = UUID.randomUUID();
        Product product = new Product("Camiseta", null, new BigDecimal("50.00"), new Category("Camisetas", null));
        product.setActive(false);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.findActiveByIdForCatalog(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
