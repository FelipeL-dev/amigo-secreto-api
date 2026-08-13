package com.projeto.amigo.secreto.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerificarEmailDto {
    @NotBlank(message = "Código é obrigatório.")
    @Pattern(
            regexp = "\\d{6}",
            message = "O código deve conter exatamente 6 dígitos."
    )
    private String codigo;
}
