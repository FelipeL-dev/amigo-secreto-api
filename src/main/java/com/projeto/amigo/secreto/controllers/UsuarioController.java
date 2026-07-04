package com.projeto.amigo.secreto.controllers;

import com.projeto.amigo.secreto.dtos.AuthResponseDTO;
import com.projeto.amigo.secreto.dtos.UsuarioDto;
import com.projeto.amigo.secreto.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping("/me")
    @Operation(summary = "buscar usuario logado")
    public ResponseEntity<UsuarioDto> getMe(){
        return(ResponseEntity.ok(usuarioService.getMe()));
    }

    @PutMapping("/me")
    @Operation(summary = "editar usuario logado")
    public ResponseEntity<AuthResponseDTO> updateMe(@RequestBody UsuarioDto dto){
        return(ResponseEntity.ok(usuarioService.updateMe(dto)));
    }
}
