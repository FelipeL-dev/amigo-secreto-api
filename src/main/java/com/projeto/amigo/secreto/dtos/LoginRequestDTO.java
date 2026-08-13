package com.projeto.amigo.secreto.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @Email(message = "Email inválido.")
    @NotBlank(message = "Email é obrigatório.")
    private String email;

    @NotBlank(message = "Senha é obrigatória.")
    private String password;
}
