package br.com.useautentica.backend.controller.publicapi;

import br.com.useautentica.backend.dto.color.ColorResponse;
import br.com.useautentica.backend.service.ColorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/colors")
public class ColorController {

    private final ColorService colorService;

    public ColorController(ColorService colorService) {
        this.colorService = colorService;
    }

    @GetMapping
    public List<ColorResponse> list() {
        return colorService.findAll();
    }
}
