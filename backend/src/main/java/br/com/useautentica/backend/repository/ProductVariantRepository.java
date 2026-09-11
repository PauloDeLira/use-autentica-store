package br.com.useautentica.backend.repository;

import br.com.useautentica.backend.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    boolean existsByProductIdAndSizeIdAndColorId(UUID productId, UUID sizeId, UUID colorId);

    boolean existsByProductIdAndSizeIdAndColorIdAndIdNot(UUID productId, UUID sizeId, UUID colorId, UUID id);

    List<ProductVariant> findByProductIdAndActiveTrue(UUID productId);

    List<ProductVariant> findByProductIdInAndActiveTrue(List<UUID> productIds);

    boolean existsByProductId(UUID productId);
}
