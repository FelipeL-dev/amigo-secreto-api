package com.projeto.amigo.secreto.repositories;


import com.projeto.amigo.secreto.entities.RefreshToken;
import com.projeto.amigo.secreto.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.usuario = :usuario")
    void deleteByUsuario(@Param("usuario") Usuario usuario);
}
