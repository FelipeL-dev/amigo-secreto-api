package com.projeto.amigo.secreto.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projeto.amigo.secreto.entities.Grupo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.time.LocalDate;

@Data
public class GrupoDTO {
    private long id;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9\\s@!#$%&*()_+=\\-.,]+$", message = "Nome contém caracteres inválidos")
    @Size(min = 3, max = 100)
    private String nome;

    private String conviteToken;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataCriacao;

    private Boolean sorteado;

    public Grupo mapToEntitie(){
        Grupo grupo = new Grupo();
        grupo.setNome(this.getNome());
        grupo.setSorteado(this.getSorteado());
        return grupo;
    }
}

