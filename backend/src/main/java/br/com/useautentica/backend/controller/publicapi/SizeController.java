package br.com.useautentica.backend.controller.publicapi;

import br.com.useautentica.backend.dto.size.SizeResponse;
import br.com.useautentica.backend.service.SizeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sizes")
public class SizeController {

    private final SizeService sizeService;

    public SizeController(SizeService sizeService) {
        this.sizeService = sizeService;
    }

    @GetMapping
    public List<SizeResponse> list() {
        return sizeService.findAll();
    }
}
