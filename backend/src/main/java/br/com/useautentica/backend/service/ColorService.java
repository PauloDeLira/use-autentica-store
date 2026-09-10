package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.color.ColorRequest;
import br.com.useautentica.backend.dto.color.ColorResponse;
import br.com.useautentica.backend.entity.Color;
import br.com.useautentica.backend.exception.BusinessException;
import br.com.useautentica.backend.exception.ResourceNotFoundException;
import br.com.useautentica.backend.repository.ColorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ColorService {

    private final ColorRepository colorRepository;

    public ColorService(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    @Transactional(readOnly = true)
    public List<ColorResponse> findAll() {
        return colorRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ColorResponse create(ColorRequest request) {
        if (colorRepository.existsByName(request.name())) {
            throw new BusinessException("COLOR_NAME_ALREADY_EXISTS", "Já existe uma cor com esse nome");
        }
        Color color = new Color(request.name(), request.hexCode());
        return toResponse(colorRepository.save(color));
    }

    @Transactional
    public ColorResponse update(UUID id, ColorRequest request) {
        Color color = findByIdOrThrow(id);
        if (colorRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new BusinessException("COLOR_NAME_ALREADY_EXISTS", "Já existe uma cor com esse nome");
        }
        color.setName(request.name());
        color.setHexCode(request.hexCode());
        return toResponse(color);
    }

    @Transactional
    public ColorResponse setActive(UUID id, boolean active) {
        Color color = findByIdOrThrow(id);
        color.setActive(active);
        return toResponse(color);
    }

    @Transactional(readOnly = true)
    public Color findActiveByIdOrThrow(UUID id) {
        Color color = findByIdOrThrow(id);
        if (!color.isActive()) {
            throw new BusinessException("COLOR_INACTIVE", "A cor informada está inativa");
        }
        return color;
    }

    private Color findByIdOrThrow(UUID id) {
        return colorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("COLOR_NOT_FOUND", "Cor não encontrada"));
    }

    private ColorResponse toResponse(Color color) {
        return new ColorResponse(color.getId(), color.getName(), color.getHexCode(), color.isActive());
    }
}
