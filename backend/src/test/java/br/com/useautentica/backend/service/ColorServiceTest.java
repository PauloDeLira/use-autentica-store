package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.color.ColorRequest;
import br.com.useautentica.backend.dto.color.ColorResponse;
import br.com.useautentica.backend.entity.Color;
import br.com.useautentica.backend.exception.BusinessException;
import br.com.useautentica.backend.exception.ResourceNotFoundException;
import br.com.useautentica.backend.repository.ColorRepository;
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
class ColorServiceTest {

    @Mock
    private ColorRepository colorRepository;

    @InjectMocks
    private ColorService colorService;

    @Test
    void createsColorWhenNameIsUnique() {
        when(colorRepository.existsByName("Preto")).thenReturn(false);
        when(colorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ColorResponse response = colorService.create(new ColorRequest("Preto", "#000000"));

        assertThat(response.name()).isEqualTo("Preto");
        assertThat(response.hexCode()).isEqualTo("#000000");
        assertThat(response.active()).isTrue();
    }

    @Test
    void rejectsCreateWhenNameAlreadyExists() {
        when(colorRepository.existsByName("Preto")).thenReturn(true);

        assertThatThrownBy(() -> colorService.create(new ColorRequest("Preto", "#000000")))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void rejectsFindActiveByIdWhenColorIsInactive() {
        UUID id = UUID.randomUUID();
        Color color = new Color("Preto", "#000000");
        color.setActive(false);
        when(colorRepository.findById(id)).thenReturn(Optional.of(color));

        assertThatThrownBy(() -> colorService.findActiveByIdOrThrow(id))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void throwsNotFoundWhenColorDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(colorRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> colorService.findActiveByIdOrThrow(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
