package com.projeto.amigo.secreto.repositories;

import com.projeto.amigo.secreto.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    List<Grupo> findBySorteado(Boolean sorteado);
    Optional<Grupo> findByTokenConvite(String tokenConvite);
}
