package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.dashboard.WhatsAppClickStatsResponse;
import br.com.useautentica.backend.entity.Product;
import br.com.useautentica.backend.entity.ProductClickEvent;
import br.com.useautentica.backend.repository.ProductClickEventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class ProductClickEventService {

    private static final int TOP_PRODUCTS_LIMIT = 5;

    private final ProductClickEventRepository productClickEventRepository;
    private final ProductService productService;

    public ProductClickEventService(
            ProductClickEventRepository productClickEventRepository,
            ProductService productService
    ) {
        this.productClickEventRepository = productClickEventRepository;
        this.productService = productService;
    }

    @Transactional
    public void register(UUID productId) {
        Product product = productService.findByIdOrThrow(productId);
        productClickEventRepository.save(new ProductClickEvent(product));
    }

    @Transactional(readOnly = true)
    public WhatsAppClickStatsResponse stats() {
        Instant startOfToday = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);

        long clicksToday = productClickEventRepository.countByCreatedAtGreaterThanEqual(startOfToday);
        long clicksThisWeek = productClickEventRepository.countByCreatedAtGreaterThanEqual(sevenDaysAgo);

        List<WhatsAppClickStatsResponse.TopProduct> topProducts = productClickEventRepository
                .findTopProducts(PageRequest.of(0, TOP_PRODUCTS_LIMIT))
                .stream()
                .map(p -> new WhatsAppClickStatsResponse.TopProduct(p.getProductId(), p.getProductName(), p.getClicks()))
                .toList();

        return new WhatsAppClickStatsResponse(clicksToday, clicksThisWeek, topProducts);
    }
}
