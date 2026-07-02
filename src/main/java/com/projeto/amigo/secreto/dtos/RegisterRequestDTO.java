package com.projeto.amigo.secreto.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO{
    @NotBlank(message = "email e obrigatorio")
    public String email;
    @NotBlank(message = "senha e obrigatoria")
    public String password;
    @NotBlank(message = "nome e obrigatorio")
    public String nome;
}
