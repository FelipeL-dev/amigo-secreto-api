package com.projeto.amigo.secreto.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.amigo.secreto.dtos.SorteioDTO;
import com.projeto.amigo.secreto.enums.StatusSorteio;
import com.projeto.amigo.secreto.security.JwtService;
import com.projeto.amigo.secreto.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
public class SorteioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SorteioService sorteioService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private PessoaService pessoaService;

    @MockitoBean
    private GrupoService grupoService;

    @MockitoBean
    private ResultadoSorteioService resultadoSorteioService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void deveBuscarTodosOsSorteios() throws Exception {
        SorteioDTO sorteio1 = SorteioDTO.builder().build();
        SorteioDTO sorteio2 = SorteioDTO.builder().build();

        when(sorteioService.findAll()).thenReturn(List.of(sorteio1, sorteio2));

        mockMvc.perform(get("/api/sorteios")).andExpect(status().isOk());
    }

    @Test
    void deveCriarSorteio() throws Exception {

        LocalDateTime dataSorteio = LocalDateTime.of(2026, 8, 14, 20, 0);

        SorteioDTO request = SorteioDTO.builder()
                .grupoId(1L)
                .dataSorteio(dataSorteio)
                .build();

        SorteioDTO response = SorteioDTO.builder()
                .id(1L)
                .status(StatusSorteio.EM_ANDAMENTO)
                .grupoId(1L)
                .dataSorteio(dataSorteio)
                .build();

        when(sorteioService.create(any(SorteioDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/sorteios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.grupoId").value(1))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"))
                .andExpect(jsonPath("$.dataSorteio").value("2026-08-14T20:00:00"));

        verify(sorteioService).create(any(SorteioDTO.class));
    }

    @Test
    void deveBuscarSorteioPorId() throws Exception {
        LocalDateTime dataSorteio = LocalDateTime.of(2026, 8, 14, 20, 0);
        SorteioDTO sorteioDTO = SorteioDTO.builder()
                .id(1L)
                .status(StatusSorteio.EM_ANDAMENTO)
                .grupoId(1L)
                .dataSorteio(dataSorteio)
                .build();

        when(sorteioService.findById(1L)).thenReturn(sorteioDTO);

        mockMvc.perform(get("/api/sorteios/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.grupoId").value(1))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"))
                .andExpect(jsonPath("$.dataSorteio").value("2026-08-14T20:00:00"));
    }

    @Test
    void deveDeletarSorteio() throws Exception {
        mockMvc.perform(delete("/api/sorteios/1")).andExpect(status().isNoContent());
    }

    @Test
    void deveFinalizarSorteio() throws Exception {

        mockMvc.perform(patch("/api/sorteios/1/finalizar")).andExpect(status().isNoContent());
        verify(sorteioService).finalizarSorteio(1L);
    }

    @Test
    void deveRealizarSorteio() throws Exception {
        mockMvc.perform(post("/api/sorteios/1/realizar")).andExpect(status().isNoContent());
        verify(sorteioService).realizarSorteio(1L);
        verify(sorteioService).finalizarSorteio(1L);
    }

}
