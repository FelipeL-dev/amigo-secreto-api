package com.projeto.amigo.secreto.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EsqueceuSenhaDTO {
    @Email
    @NotBlank
    private String email;
}
