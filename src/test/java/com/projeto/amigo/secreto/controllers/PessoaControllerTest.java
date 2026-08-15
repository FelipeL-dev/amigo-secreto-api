package com.projeto.amigo.secreto.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.amigo.secreto.dtos.PessoaDTO;
import com.projeto.amigo.secreto.security.JwtService;
import com.projeto.amigo.secreto.service.PessoaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PessoaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PessoaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PessoaService pessoaService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void  deveBuscarTodasAsPessoas() throws Exception {
        PessoaDTO pessoa1 = PessoaDTO.builder().id(1L).nome("teste1").build();
        PessoaDTO pessoa2 = PessoaDTO.builder().id(2L).nome("teste2").build();

        when(pessoaService.findAll()).thenReturn(List.of(pessoa1, pessoa2));

        mockMvc.perform(
                get("/api/pessoas")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].nome").value("teste1"))
                .andExpect(jsonPath("$[1].nome").value("teste2"));

    }

    @Test
    void deveBuscarPessoaPorId() throws Exception {
        PessoaDTO pessoaDTO = PessoaDTO.builder().id(1L).nome("teste").email("teste@email.com").build();
        when(pessoaService.findById(1L)).thenReturn(pessoaDTO);

        mockMvc.perform(
                get("/api/pessoas/1")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("teste"))
                .andExpect(jsonPath("$.email").value("teste@email.com"));
    }

    @Test
    void deveAtualizarPessoa() throws Exception {
        PessoaDTO pessoaDTO = PessoaDTO.builder().email("teste@email.com").nome("pessoa atualizada").id(1L).build();

        when(pessoaService.update(pessoaDTO, 1L)).thenReturn(pessoaDTO);
        mockMvc.perform(
                put("/api/pessoas/1").contentType(MediaType.APPLICATION_JSON).content("""
                                        {
                                            "id": 1,
                                            "nome": "pessoa atualizada",
                                            "email": "teste@email.com"
                                        }
                                        """)
        ).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.nome").value("pessoa atualizada"));
        verify(pessoaService).update(pessoaDTO, 1L);
    }

    @Test
    void deveDeletarPessoa() throws Exception {
        mockMvc.perform(delete("/api/pessoas/1")).andExpect(status().isNoContent());
        verify(pessoaService).delete(1L);
    }

}
