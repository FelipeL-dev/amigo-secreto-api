package com.projeto.amigo.secreto.controllers;

import com.projeto.amigo.secreto.dtos.ResultadoSorteioDTO;
import com.projeto.amigo.secreto.service.ResultadoSorteioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resultadosorteio")
public class ResultadoSorteioController {
    private final ResultadoSorteioService resultadoSorteioService;

    public ResultadoSorteioController(ResultadoSorteioService resultadoSorteioService) {
        this.resultadoSorteioService = resultadoSorteioService;
    }
    @Operation(summary = "Buscar resultados por grupo", description = "Retorna todos os resultados de sorteio de um determinado grupo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/sorteio/{sorteioId}")
    public ResponseEntity<List<ResultadoSorteioDTO>> getResultadoBySorteio(@PathVariable long sorteioId){
        List<ResultadoSorteioDTO> resultadosSorteios = resultadoSorteioService.findAllBySorteio(sorteioId);
        return ResponseEntity.ok(resultadosSorteios);
    }

    @Operation(summary = "Buscar resultado por ID", description = "Retorna o resultado de sorteio com o ID especificado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Resultado não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResultadoSorteioDTO> getResultadoById(@PathVariable long id){
        return ResponseEntity.ok(resultadoSorteioService.findById(id));
    }

    @Operation(summary = "Deletar resultado de sorteio", description = "Exclui um resultado de sorteio com base no seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Resultado deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Resultado não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResultadoSorteio(@PathVariable long id){
        resultadoSorteioService.deleteResultadoSorteio(id);
        return ResponseEntity.noContent().build();
    }


}
