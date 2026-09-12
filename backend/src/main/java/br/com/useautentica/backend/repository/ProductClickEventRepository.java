package br.com.useautentica.backend.repository;

import br.com.useautentica.backend.entity.ProductClickEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ProductClickEventRepository extends JpaRepository<ProductClickEvent, UUID> {

    long countByCreatedAtGreaterThanEqual(Instant since);

    @Query("""
            SELECT e.product.id AS productId, e.product.name AS productName, COUNT(e) AS clicks
            FROM ProductClickEvent e
            GROUP BY e.product.id, e.product.name
            ORDER BY COUNT(e) DESC
            """)
    List<TopProductClicks> findTopProducts(Pageable pageable);

    interface TopProductClicks {
        UUID getProductId();

        String getProductName();

        long getClicks();
    }
}
