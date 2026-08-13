package com.projeto.amigo.secreto.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReenviarEmailDto {
    @Email(message = "Email inválido.")
    @NotBlank(message = "Email é obrigatório.")
    private String email;
}