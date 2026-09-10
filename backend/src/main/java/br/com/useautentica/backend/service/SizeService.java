package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.size.SizeRequest;
import br.com.useautentica.backend.dto.size.SizeResponse;
import br.com.useautentica.backend.entity.Size;
import br.com.useautentica.backend.exception.BusinessException;
import br.com.useautentica.backend.exception.ResourceNotFoundException;
import br.com.useautentica.backend.repository.SizeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SizeService {

    private final SizeRepository sizeRepository;

    public SizeService(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    @Transactional(readOnly = true)
    public List<SizeResponse> findAll() {
        return sizeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SizeResponse create(SizeRequest request) {
        if (sizeRepository.existsByName(request.name())) {
            throw new BusinessException("SIZE_NAME_ALREADY_EXISTS", "Já existe um tamanho com esse nome");
        }
        Size size = new Size(request.name(), request.displayOrder());
        return toResponse(sizeRepository.save(size));
    }

    @Transactional
    public SizeResponse update(UUID id, SizeRequest request) {
        Size size = findByIdOrThrow(id);
        if (sizeRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new BusinessException("SIZE_NAME_ALREADY_EXISTS", "Já existe um tamanho com esse nome");
        }
        size.setName(request.name());
        size.setDisplayOrder(request.displayOrder());
        return toResponse(size);
    }

    @Transactional
    public SizeResponse setActive(UUID id, boolean active) {
        Size size = findByIdOrThrow(id);
        size.setActive(active);
        return toResponse(size);
    }

    @Transactional(readOnly = true)
    public Size findActiveByIdOrThrow(UUID id) {
        Size size = findByIdOrThrow(id);
        if (!size.isActive()) {
            throw new BusinessException("SIZE_INACTIVE", "O tamanho informado está inativo");
        }
        return size;
    }

    private Size findByIdOrThrow(UUID id) {
        return sizeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SIZE_NOT_FOUND", "Tamanho não encontrado"));
    }

    private SizeResponse toResponse(Size size) {
        return new SizeResponse(size.getId(), size.getName(), size.getDisplayOrder(), size.isActive());
    }
}
