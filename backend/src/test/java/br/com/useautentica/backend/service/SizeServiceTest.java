package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.size.SizeRequest;
import br.com.useautentica.backend.dto.size.SizeResponse;
import br.com.useautentica.backend.entity.Size;
import br.com.useautentica.backend.exception.BusinessException;
import br.com.useautentica.backend.exception.ResourceNotFoundException;
import br.com.useautentica.backend.repository.SizeRepository;
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
class SizeServiceTest {

    @Mock
    private SizeRepository sizeRepository;

    @InjectMocks
    private SizeService sizeService;

    @Test
    void createsSizeWhenNameIsUnique() {
        when(sizeRepository.existsByName("M")).thenReturn(false);
        when(sizeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SizeResponse response = sizeService.create(new SizeRequest("M", 2));

        assertThat(response.name()).isEqualTo("M");
        assertThat(response.displayOrder()).isEqualTo(2);
        assertThat(response.active()).isTrue();
    }

    @Test
    void rejectsCreateWhenNameAlreadyExists() {
        when(sizeRepository.existsByName("M")).thenReturn(true);

        assertThatThrownBy(() -> sizeService.create(new SizeRequest("M", 2)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void rejectsFindActiveByIdWhenSizeIsInactive() {
        UUID id = UUID.randomUUID();
        Size size = new Size("M", 2);
        size.setActive(false);
        when(sizeRepository.findById(id)).thenReturn(Optional.of(size));

        assertThatThrownBy(() -> sizeService.findActiveByIdOrThrow(id))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void throwsNotFoundWhenSizeDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(sizeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sizeService.findActiveByIdOrThrow(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
