package com.projeto.amigo.secreto.entities;

import com.projeto.amigo.secreto.dtos.PessoaDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pessoa {
    @Id
    @GeneratedValue
    long id;
    String nome;
    String email;

    @ManyToMany
    @JoinTable(
            name = "pessoa_grupo",
            joinColumns = @JoinColumn(name = "pessoa_id"),
            inverseJoinColumns = @JoinColumn(name = "grupo_id")
    )
    private List<Grupo> grupos;

    public Pessoa(int i, String b) {
        this.id = i;
        this.nome = b;
    }

    public PessoaDTO mapToDto() {
        PessoaDTO dto = new PessoaDTO();
        dto.setId(this.getId());
        dto.setNome(this.getNome());
        dto.setEmail(this.getEmail());

        if (this.getGrupos() != null) {
            dto.setGrupoIds(this.getGrupos().stream()
                    .map(Grupo::getId)
                    .toList());
        }

        return dto;
    }

    public void updatePessoa(String nome, String email){
        this.nome = nome;
        this.email = email;
    }
}
