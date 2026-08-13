package com.projeto.amigo.secreto.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.amigo.secreto.dtos.GrupoDTO;
import com.projeto.amigo.secreto.dtos.PessoaDTO;
import com.projeto.amigo.secreto.security.JwtService;
import com.projeto.amigo.secreto.service.GrupoService;
import com.projeto.amigo.secreto.service.PessoaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GrupoController.class)
@AutoConfigureMockMvc(addFilters = false)
class GrupoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GrupoService grupoService;

    @MockitoBean
    private PessoaService pessoaService;

    @MockitoBean
    private JwtService jwtService;


    @Test
    void deveCriarGrupo() throws Exception {

        GrupoDTO request = GrupoDTO.builder()
                .nome("Grupo Teste")
                .build();

        GrupoDTO response = GrupoDTO.builder()
                .id(1L)
                .nome("Grupo Teste")
                .build();

        when(grupoService.create(any(GrupoDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/grupos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Grupo Teste"));

        verify(grupoService).create(any(GrupoDTO.class));
    }


    @Test
    void deveListarTodosOsGrupos() throws Exception {

        GrupoDTO grupo1 = GrupoDTO.builder()
                .id(1L)
                .nome("Grupo 1")
                .build();

        GrupoDTO grupo2 = GrupoDTO.builder()
                .id(2L)
                .nome("Grupo 2")
                .build();

        when(grupoService.findAll())
                .thenReturn(List.of(grupo1, grupo2));

        mockMvc.perform(
                        get("/api/grupos")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Grupo 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Grupo 2"));

        verify(grupoService).findAll();
    }


    @Test
    void deveBuscarGrupoPorId() throws Exception {

        GrupoDTO response = GrupoDTO.builder()
                .id(1L)
                .nome("Grupo Teste")
                .build();

        when(grupoService.findById(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/grupos/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Grupo Teste"));

        verify(grupoService).findById(1L);
    }


    @Test
    void deveDeletarGrupo() throws Exception {

        mockMvc.perform(
                        delete("/api/grupos/1")
                )
                .andExpect(status().isNoContent());

        verify(grupoService).delete(1L);
    }


    @Test
    void deveAtualizarGrupo() throws Exception {

        GrupoDTO response = GrupoDTO.builder()
                .id(1L)
                .nome("Grupo Atualizado")
                .build();

        when(grupoService.update(1L, "Grupo Atualizado"))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/grupos/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "nome": "Grupo Atualizado"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Grupo Atualizado"));

        verify(grupoService).update(1L, "Grupo Atualizado");
    }


    @Test
    void deveGerarConvite() throws Exception {

        when(grupoService.gerarConviteToken(1L))
                .thenReturn("abc123");

        mockMvc.perform(
                        post("/api/grupos/1/convite")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("abc123"));

        verify(grupoService).gerarConviteToken(1L);
    }


    @Test
    void deveEntrarNoGrupoPorConvite() throws Exception {

        mockMvc.perform(
                        post("/api/grupos/entrar/abc123")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Voce entoru no grupo com sucesso!"));

        verify(grupoService).entrarNoGrupo("abc123");
    }


    @Test
    void deveListarPessoasDoGrupo() throws Exception {

        PessoaDTO pessoa1 = PessoaDTO.builder()
                .id(1L)
                .nome("João")
                .build();

        PessoaDTO pessoa2 = PessoaDTO.builder()
                .id(2L)
                .nome("Maria")
                .build();

        when(pessoaService.findByGrupoId(1L))
                .thenReturn(List.of(pessoa1, pessoa2));

        mockMvc.perform(
                        get("/api/grupos/1/pessoas")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("João"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Maria"));

        verify(pessoaService).findByGrupoId(1L);
    }


    @Test
    void deveListarGruposSorteados() throws Exception {

        GrupoDTO grupo1 = GrupoDTO.builder()
                .id(1L)
                .nome("Grupo Sorteado 1")
                .build();

        GrupoDTO grupo2 = GrupoDTO.builder()
                .id(2L)
                .nome("Grupo Sorteado 2")
                .build();

        when(grupoService.findGruposSorteados())
                .thenReturn(List.of(grupo1, grupo2));

        mockMvc.perform(
                        get("/api/grupos/sorteados")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Grupo Sorteado 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Grupo Sorteado 2"));

        verify(grupoService).findGruposSorteados();
    }


    @Test
    void deveAdicionarPessoaAoGrupo() throws Exception {

        mockMvc.perform(
                        post("/api/grupos/1/pessoas/2")
                )
                .andExpect(status().isNoContent());

        verify(pessoaService).adicionarPessoaAGrupo(1L, 2L);
    }


    @Test
    void deveListarMeusGrupos() throws Exception {

        GrupoDTO grupo1 = GrupoDTO.builder()
                .id(1L)
                .nome("Meu Grupo 1")
                .build();

        GrupoDTO grupo2 = GrupoDTO.builder()
                .id(2L)
                .nome("Meu Grupo 2")
                .build();

        when(grupoService.findMeusGrupos())
                .thenReturn(List.of(grupo1, grupo2));

        mockMvc.perform(
                        get("/api/grupos/meus")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Meu Grupo 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Meu Grupo 2"));

        verify(grupoService).findMeusGrupos();
    }
}