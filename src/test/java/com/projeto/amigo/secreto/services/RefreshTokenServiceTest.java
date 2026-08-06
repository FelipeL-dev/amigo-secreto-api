package com.projeto.amigo.secreto.services;

import com.projeto.amigo.secreto.entities.RefreshToken;
import com.projeto.amigo.secreto.entities.Usuario;
import com.projeto.amigo.secreto.exceptions.BusinessException;
import com.projeto.amigo.secreto.exceptions.NotFoundException;
import com.projeto.amigo.secreto.repositories.RefreshTokenRepository;
import com.projeto.amigo.secreto.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    RefreshTokenService refreshTokenService;

    @Test
    void deveCriarRefreshToken() {
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .build();

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken token = refreshTokenService.createRefreshToken(usuario);

        assertNotNull(token.getToken());
        assertEquals(usuario, token.getUsuario());
        assertTrue(token.getExpiryDate().isAfter(Instant.now()));

        verify(refreshTokenRepository).deleteByUsuario(usuario);
        verify(refreshTokenRepository).flush();
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void deveValidarRefreshToken() {
        RefreshToken token = RefreshToken.builder()
                .token("123456")
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        when(refreshTokenRepository.findByToken("123456"))
                .thenReturn(Optional.of(token));

        RefreshToken response =
                refreshTokenService.validateRefreshToken("123456");

        assertEquals(token, response);

        verify(refreshTokenRepository).findByToken("123456");
    }

    @Test
    void deveLancarNotFoundAoValidarRefreshToken() {

        when(refreshTokenRepository.findByToken("123456"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> refreshTokenService.validateRefreshToken("123456")
        );
    }

    @Test
    void deveLancarBusinessExceptionQuandoRefreshTokenExpirar() {

        RefreshToken token = RefreshToken.builder()
                .token("123456")
                .expiryDate(Instant.now().minusSeconds(60))
                .build();

        when(refreshTokenRepository.findByToken("123456"))
                .thenReturn(Optional.of(token));

        assertThrows(
                BusinessException.class,
                () -> refreshTokenService.validateRefreshToken("123456")
        );

        verify(refreshTokenRepository).delete(token);
    }
}
