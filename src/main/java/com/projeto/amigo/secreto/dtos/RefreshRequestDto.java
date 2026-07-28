package com.projeto.amigo.secreto.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequestDto {
    @NotBlank(message = "Refresh token é obrigatório.")
    private String refreshToken;
}
