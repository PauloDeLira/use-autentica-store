package br.com.useautentica.backend.controller.admin;

import br.com.useautentica.backend.dto.dashboard.WhatsAppClickStatsResponse;
import br.com.useautentica.backend.service.ProductClickEventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardAdminController {

    private final ProductClickEventService productClickEventService;

    public DashboardAdminController(ProductClickEventService productClickEventService) {
        this.productClickEventService = productClickEventService;
    }

    @GetMapping("/whatsapp-clicks")
    public WhatsAppClickStatsResponse whatsAppClicks() {
        return productClickEventService.stats();
    }
}
