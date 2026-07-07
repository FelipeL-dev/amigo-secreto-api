package com.projeto.amigo.secreto.repositories;

import com.projeto.amigo.secreto.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByCodigoVerificacao(String codigoVerificacao);
}