package br.com.useautentica.backend.dto.dashboard;

import java.util.List;
import java.util.UUID;

public record WhatsAppClickStatsResponse(
        long clicksToday,
        long clicksThisWeek,
        List<TopProduct> topProducts
) {
    public record TopProduct(UUID productId, String productName, long clicks) {
    }
}
