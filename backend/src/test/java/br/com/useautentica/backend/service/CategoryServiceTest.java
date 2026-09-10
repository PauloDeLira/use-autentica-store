package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.category.CategoryRequest;
import br.com.useautentica.backend.dto.category.CategoryResponse;
import br.com.useautentica.backend.entity.Category;
import br.com.useautentica.backend.exception.BusinessException;
import br.com.useautentica.backend.exception.ResourceNotFoundException;
import br.com.useautentica.backend.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createsCategoryWhenNameIsUnique() {
        when(categoryRepository.existsByName("Camisetas")).thenReturn(false);
        when(categoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponse response = categoryService.create(new CategoryRequest("Camisetas", "Peças de cima"));

        assertThat(response.name()).isEqualTo("Camisetas");
        assertThat(response.active()).isTrue();
    }

    @Test
    void rejectsCreateWhenNameAlreadyExists() {
        when(categoryRepository.existsByName("Camisetas")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(new CategoryRequest("Camisetas", null)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void rejectsFindActiveByIdWhenCategoryIsInactive() {
        UUID id = UUID.randomUUID();
        Category category = new Category("Camisetas", null);
        category.setActive(false);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> categoryService.findActiveByIdOrThrow(id))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void throwsNotFoundWhenCategoryDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findActiveByIdOrThrow(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
