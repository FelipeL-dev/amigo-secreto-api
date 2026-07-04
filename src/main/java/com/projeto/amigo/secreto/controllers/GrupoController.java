package com.projeto.amigo.secreto.controllers;

import com.projeto.amigo.secreto.dtos.GrupoDTO;
import com.projeto.amigo.secreto.dtos.PessoaDTO;
import com.projeto.amigo.secreto.service.GrupoService;
import com.projeto.amigo.secreto.service.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grupos")

public class GrupoController {
    private final PessoaService pessoaService;
    private final GrupoService grupoService;

    public GrupoController(PessoaService pessoaService, GrupoService grupoService) {
        this.pessoaService = pessoaService;
        this.grupoService = grupoService;
    }


    @Operation(summary = "Criar um grupo no sistema", description = "Cria um grupo no sistema, através das informações enviadas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupo criado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor"),
            @ApiResponse(responseCode = "400", description = "Falha ao criar grupo")
    })
    @PostMapping
    public ResponseEntity<GrupoDTO> createGrupo(@Valid @RequestBody GrupoDTO dto){
        return ResponseEntity.ok(grupoService.create(dto));
    }



    @Operation(summary = "Listar todas os grupos", description = "retorna uma lista com todos os grupos cadastrados na API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public List<GrupoDTO> getAllGrupos(){
        return grupoService.findAll();
    }


    @Operation(summary = "Buscar grupo por ID", description = "Retorna um grupo com o id selecionado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupo encontrado"),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GrupoDTO> getGrupoById(@PathVariable Long id){
        return ResponseEntity.ok(grupoService.findById(id));
    }


    @Operation(summary = "Excluir um grupo do sistema", description = "Deleta um grupo do sistema, baseando-se no seu id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Grupo deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrupo(@PathVariable Long id){
        grupoService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Editar um grupo existente", description = "Edita um grupo existente, baseando-se no seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupo editado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Falha na edição do grupo"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GrupoDTO> updateGrupo(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(grupoService.update(id, body.get("nome")));
    }

    @Operation(summary = "Gerar convite de grupo", description = "Gera o link do convite para um grupo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Convite gerado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Falha na geracao do convite"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/{id}/convite")
    public ResponseEntity<String> createConvite(@PathVariable Long id) {
        return ResponseEntity.ok(grupoService.gerarConviteToken(id));
    }

    @Operation(summary = "Adicionar pessoa ao grupo pelo link de convite", description = "adiciona a pessoa ao grupo pelo convite")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pessoa adicionada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Falha do convite"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/entrar/{token}")
    public ResponseEntity<String> addPessoaGrupoInv(@PathVariable String token) {
        grupoService.entrarNoGrupo(token);
        return ResponseEntity.ok("Voce entoru no grupo com sucesso!");
    }

    @Operation(summary = "Listar pessoas presentes em um grupo", description = "Lista as pessoas presentes em um grupo especificado pelo id do grupo.")
    @GetMapping("/{grupoId}/pessoas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor"),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado")
    })
    public ResponseEntity<List<PessoaDTO>> getPessoasByGrupoId(@PathVariable long grupoId) {
        List<PessoaDTO> pessoas = pessoaService.findByGrupoId(grupoId);
        return ResponseEntity.ok(pessoas);
    }

    @GetMapping("/sorteados")
    public ResponseEntity<List<GrupoDTO>> getGruposBySorteado(){
        List<GrupoDTO> grupos = grupoService.findGruposSorteados();
        return ResponseEntity.ok(grupos);
    }

    @PostMapping("/{grupoId}/pessoas/{pessoaId}")
    @Operation(summary = "Adicionar pessoa ao grupo", description = "Associa uma pessoa existente a um grupo existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pessoa adicionada com sucesso ao grupo"),
            @ApiResponse(responseCode = "404", description = "Pessoa ou grupo não encontrados"),
            @ApiResponse(responseCode = "400", description = "Pessoa já está associada ao grupo")
    })
    public ResponseEntity<Void> adicionarPessoaAoGrupo(@PathVariable Long grupoId, @PathVariable Long pessoaId) {
        pessoaService.adicionarPessoaAGrupo(grupoId, pessoaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/meus")
    @Operation(summary = "Listar meus grupos", description = "Retorna os grupos do usuário logado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<GrupoDTO>> getMeusGrupos() {
        return ResponseEntity.ok(grupoService.findMeusGrupos());
    }

}
