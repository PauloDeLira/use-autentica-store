package br.com.useautentica.backend.controller.admin;

import br.com.useautentica.backend.dto.ActiveStatusRequest;
import br.com.useautentica.backend.dto.color.ColorRequest;
import br.com.useautentica.backend.dto.color.ColorResponse;
import br.com.useautentica.backend.service.ColorService;
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
@RequestMapping("/api/admin/colors")
public class ColorAdminController {

    private final ColorService colorService;

    public ColorAdminController(ColorService colorService) {
        this.colorService = colorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ColorResponse create(@Valid @RequestBody ColorRequest request) {
        return colorService.create(request);
    }

    @PutMapping("/{id}")
    public ColorResponse update(@PathVariable UUID id, @Valid @RequestBody ColorRequest request) {
        return colorService.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public ColorResponse setActive(@PathVariable UUID id, @Valid @RequestBody ActiveStatusRequest request) {
        return colorService.setActive(id, request.active());
    }
}
