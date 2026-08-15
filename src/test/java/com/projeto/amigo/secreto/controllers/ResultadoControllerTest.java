package com.projeto.amigo.secreto.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.amigo.secreto.dtos.ResultadoSorteioDTO;
import com.projeto.amigo.secreto.security.JwtService;
import com.projeto.amigo.secreto.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
public class ResultadoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private ResultadoSorteioService resultadoSorteioService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private PessoaService pessoaService;

    @MockitoBean
    private GrupoService grupoService;

    @MockitoBean
    private SorteioService sorteioService;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void deveBuscarResultadoPorSorteio() throws Exception {
        ResultadoSorteioDTO resultadoSorteio1 = ResultadoSorteioDTO.builder().id(1).sorteio_id(1).sorteado_id(1).sorteador_id(2).build();
        ResultadoSorteioDTO resultadoSorteio2 = ResultadoSorteioDTO.builder().id(2).sorteio_id(1).sorteado_id(2).sorteador_id(1).build();

        when(resultadoSorteioService.findAllBySorteio(1L)).thenReturn(List.of(resultadoSorteio1, resultadoSorteio2));
        mockMvc.perform(
                        get("/api/resultadosorteio/sorteio/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].sorteio_id").value(1))
                .andExpect(jsonPath("$[1].sorteio_id").value(1))
                .andExpect(jsonPath("$[0].sorteado_id").value(1))
                .andExpect(jsonPath("$[1].sorteado_id").value(2))
                .andExpect(jsonPath("$[0].sorteador_id").value(2))
                .andExpect(jsonPath("$[1].sorteador_id").value(1));
    }

    @Test
    void deveBuscarResultadoPorId() throws Exception {
        ResultadoSorteioDTO resultadoSorteioDTO = ResultadoSorteioDTO.builder().build();

        when(resultadoSorteioService.findById(1L)).thenReturn(resultadoSorteioDTO);

        mockMvc.perform(get("/api/resultadosorteio/1")).andExpect(status().isOk());
    }

    @Test
    void deveDeletarResultado() throws  Exception{
        mockMvc.perform(delete("/api/resultadosorteio/1")).andExpect(status().isNoContent());
    }


}
