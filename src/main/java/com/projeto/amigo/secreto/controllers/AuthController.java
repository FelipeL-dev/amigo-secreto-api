package com.projeto.amigo.secreto.controllers;

import com.projeto.amigo.secreto.dtos.*;
import com.projeto.amigo.secreto.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar um usuário", description = "Cria um novo usuário no sistema e retorna um token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Falha ao registrar usuário"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna um token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token", description = "Recebe um refresh token e retorna um novo access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Refresh token inválido ou expirado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody RefreshRequestDto request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/verificar")
    @Operation(summary = "Verificar email", description = "Verifica o email do usuário pelo código recebido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verificado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Código inválido ou expirado"),
            @ApiResponse(responseCode = "404", description = "Código não encontrado")
    })
    public ResponseEntity<Void> verificarEmail(@RequestBody VerificarEmailDto dto) {
        authService.verificarEmail(dto.getCodigo());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reenviar-verificacao")
    @Operation(summary = "Reenviar código de verificação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Código reenviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email já verificado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> reenviarVerificacao(@RequestBody ReenviarEmailDto dto) {
        authService.reenviarCodigoVerificacao(dto.getEmail());
        return ResponseEntity.ok().build();
    }
}