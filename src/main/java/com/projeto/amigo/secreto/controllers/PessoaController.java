package com.projeto.amigo.secreto.controllers;

import com.projeto.amigo.secreto.dtos.PessoaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.projeto.amigo.secreto.service.PessoaService;

import java.util.List;

@RestController
@RequestMapping("/api/pessoas")
public class PessoaController {
    private final PessoaService pessoaService;

    public PessoaController(PessoaService service) {
        this.pessoaService = service;
    }

    @Operation(summary = "Listar todas as pessoas", description = "retorna uma lista com todas as pessoas cadastradas na API")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public List<PessoaDTO> getAllPessoas(){
        return pessoaService.findAll();
    }

    @Operation(summary = "Buscar pessoa por ID", description = "Retorna uma pessoa com o id selecionado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pessoa encontrada"),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PessoaDTO> getPessoaById(@PathVariable Long id){
        return ResponseEntity.ok(pessoaService.findById(id));
    }


    @Operation(summary = "Atualiza os dados de uma pessoa", description = "Edita as informações de uma pessoa existente, baseando-se no seu id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Informações atualizadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PessoaDTO> updatePessoa(@PathVariable Long id, @Valid @RequestBody PessoaDTO dto){
        PessoaDTO existing = pessoaService.update(dto, id);
        return ResponseEntity.ok(existing);
    }

    @Operation(summary = "Excluir uma pessoa do sistema", description = "Deleta uma pessoa do sistema, baseando-se no seu id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pessoa deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePessoa(@PathVariable Long id){
        pessoaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
