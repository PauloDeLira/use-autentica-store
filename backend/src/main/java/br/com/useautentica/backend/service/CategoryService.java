package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.category.CategoryRequest;
import br.com.useautentica.backend.dto.category.CategoryResponse;
import br.com.useautentica.backend.entity.Category;
import br.com.useautentica.backend.exception.BusinessException;
import br.com.useautentica.backend.exception.ResourceNotFoundException;
import br.com.useautentica.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new BusinessException("CATEGORY_NAME_ALREADY_EXISTS", "Já existe uma categoria com esse nome");
        }
        Category category = new Category(request.name(), request.description());
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = findByIdOrThrow(id);
        if (categoryRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new BusinessException("CATEGORY_NAME_ALREADY_EXISTS", "Já existe uma categoria com esse nome");
        }
        category.setName(request.name());
        category.setDescription(request.description());
        return toResponse(category);
    }

    @Transactional
    public CategoryResponse setActive(UUID id, boolean active) {
        Category category = findByIdOrThrow(id);
        category.setActive(active);
        return toResponse(category);
    }

    @Transactional(readOnly = true)
    public Category findActiveByIdOrThrow(UUID id) {
        Category category = findByIdOrThrow(id);
        if (!category.isActive()) {
            throw new BusinessException("CATEGORY_INACTIVE", "A categoria informada está inativa");
        }
        return category;
    }

    private Category findByIdOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CATEGORY_NOT_FOUND", "Categoria não encontrada"));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription(), category.isActive());
    }
}
