package com.projeto.amigo.secreto.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedefinirSenhaDTO {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Pattern(
            regexp = "\\d{6}",
            message = "O código deve conter exatamente 6 dígitos."
    )
    private String codigo;
    @NotBlank
    private String senha;

}
