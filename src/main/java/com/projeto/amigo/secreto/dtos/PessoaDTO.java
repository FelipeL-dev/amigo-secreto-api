package com.projeto.amigo.secreto.dtos;

import com.projeto.amigo.secreto.entities.Grupo;
import com.projeto.amigo.secreto.entities.Pessoa;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PessoaDTO {
    private long id;

    @NotBlank
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ\\s]+$", message = "Nome deve conter apenas letras e espaços")
    @Size(min = 3, max = 100)
    private String nome;

    @NotBlank
    @Email
    private String email;


    private List<Long> grupoIds;

    public Pessoa mapToEntitie(List<Grupo> grupos) {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(this.nome);
        pessoa.setEmail(this.email);
        pessoa.setGrupos(grupos);
        return pessoa;
    }
}
