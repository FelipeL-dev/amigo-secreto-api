package com.projeto.amigo.secreto.repositories;

import com.projeto.amigo.secreto.entities.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    List<Pessoa> findAllByGrupos_Id(Long grupoId);
    Pessoa findByNome(String nome);
}
