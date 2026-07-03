package com.projeto.amigo.secreto.service;

import com.projeto.amigo.secreto.entities.RefreshToken;
import com.projeto.amigo.secreto.entities.Usuario;
import com.projeto.amigo.secreto.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60;

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(Usuario usuario){
        refreshTokenRepository.deleteByUsuario(usuario);

        RefreshToken refreshToken = RefreshToken.builder().usuario(usuario).token(UUID.randomUUID().toString()).expiryDate(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRATION)).build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token){
        RefreshToken refreshToken= refreshTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("RefreshToken nao encontrado"));

        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expirado, faca login novamente");
        }
        return refreshToken;
    }

}
