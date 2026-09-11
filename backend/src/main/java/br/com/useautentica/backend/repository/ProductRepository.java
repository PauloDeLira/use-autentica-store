package br.com.useautentica.backend.repository;

import br.com.useautentica.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByActiveTrue();

    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:sizeId IS NULL OR EXISTS (
                    SELECT 1 FROM ProductVariant v
                    WHERE v.product = p AND v.active = true AND v.size.id = :sizeId))
              AND (:colorId IS NULL OR EXISTS (
                    SELECT 1 FROM ProductVariant v
                    WHERE v.product = p AND v.active = true AND v.color.id = :colorId))
            """)
    List<Product> findActiveForCatalog(
            @Param("categoryId") UUID categoryId,
            @Param("sizeId") UUID sizeId,
            @Param("colorId") UUID colorId
    );
}
