package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.productimage.ProductImageResponse;
import br.com.useautentica.backend.entity.Category;
import br.com.useautentica.backend.entity.Product;
import br.com.useautentica.backend.entity.ProductImage;
import br.com.useautentica.backend.exception.BusinessException;
import br.com.useautentica.backend.exception.ResourceNotFoundException;
import br.com.useautentica.backend.repository.ProductImageRepository;
import br.com.useautentica.backend.storage.ImageStorageService;
import br.com.useautentica.backend.storage.StoredFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductImageServiceTest {

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ImageStorageService imageStorageService;

    @InjectMocks
    private ProductImageService productImageService;

    private Product product() {
        return new Product("Camiseta Oversized", null, new BigDecimal("89.90"), new Category("Camisetas", null));
    }

    private Product productWithId(UUID id) {
        Product product = product();
        try {
            var field = Product.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(product, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return product;
    }

    // Assinatura real de JPEG (SOI + APP0): a validação agora inspeciona os
    // bytes do arquivo, não o Content-Type declarado.
    private static final byte[] JPEG_BYTES = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0, 0, 0};

    @Test
    void uploadsImageAndComputesDisplayOrder() {
        UUID productId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", JPEG_BYTES);

        when(productService.findByIdOrThrow(productId)).thenReturn(product());
        when(productImageRepository.countByProductId(productId)).thenReturn(2);
        when(imageStorageService.store(file, ".jpg")).thenReturn(new StoredFile("abc.jpg", "/uploads/abc.jpg"));
        when(productImageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductImageResponse response = productImageService.upload(productId, file, "Foto da camiseta");

        assertThat(response.url()).isEqualTo("/uploads/abc.jpg");
        assertThat(response.altText()).isEqualTo("Foto da camiseta");
        assertThat(response.displayOrder()).isEqualTo(2);
    }

    @Test
    void rejectsUnsupportedFormat() {
        UUID productId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "arquivo.pdf", "application/pdf", new byte[]{1});
        when(productService.findByIdOrThrow(productId)).thenReturn(product());

        assertThatThrownBy(() -> productImageService.upload(productId, file, null))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void rejectsEmptyFile() {
        UUID productId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", new byte[0]);
        when(productService.findByIdOrThrow(productId)).thenReturn(product());

        assertThatThrownBy(() -> productImageService.upload(productId, file, null))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void deletesImageAndUnderlyingFile() {
        UUID productId = UUID.randomUUID();
        UUID imageId = UUID.randomUUID();
        Product product = productWithId(productId);
        ProductImage image = new ProductImage(product, "abc.jpg", "/uploads/abc.jpg", null, 0);
        when(productImageRepository.findById(imageId)).thenReturn(Optional.of(image));

        productImageService.delete(productId, imageId);

        verify(imageStorageService).delete("abc.jpg");
        verify(productImageRepository).delete(image);
    }

    @Test
    void rejectsDeleteWhenImageBelongsToAnotherProduct() {
        UUID productId = UUID.randomUUID();
        UUID otherProductId = UUID.randomUUID();
        UUID imageId = UUID.randomUUID();
        Product otherProduct = productWithId(otherProductId);
        ProductImage image = new ProductImage(otherProduct, "abc.jpg", "/uploads/abc.jpg", null, 0);
        when(productImageRepository.findById(imageId)).thenReturn(Optional.of(image));

        assertThatThrownBy(() -> productImageService.delete(productId, imageId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
