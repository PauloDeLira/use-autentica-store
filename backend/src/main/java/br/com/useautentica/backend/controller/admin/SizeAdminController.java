package br.com.useautentica.backend.controller.admin;

import br.com.useautentica.backend.dto.ActiveStatusRequest;
import br.com.useautentica.backend.dto.size.SizeRequest;
import br.com.useautentica.backend.dto.size.SizeResponse;
import br.com.useautentica.backend.service.SizeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/sizes")
public class SizeAdminController {

    private final SizeService sizeService;

    public SizeAdminController(SizeService sizeService) {
        this.sizeService = sizeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SizeResponse create(@Valid @RequestBody SizeRequest request) {
        return sizeService.create(request);
    }

    @PutMapping("/{id}")
    public SizeResponse update(@PathVariable UUID id, @Valid @RequestBody SizeRequest request) {
        return sizeService.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public SizeResponse setActive(@PathVariable UUID id, @Valid @RequestBody ActiveStatusRequest request) {
        return sizeService.setActive(id, request.active());
    }
}
