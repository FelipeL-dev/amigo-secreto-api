package com.projeto.amigo.secreto.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.amigo.secreto.dtos.AuthResponseDTO;
import com.projeto.amigo.secreto.dtos.UsuarioDto;
import com.projeto.amigo.secreto.security.JwtService;
import com.projeto.amigo.secreto.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private GrupoController grupoController;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void deveBuscarUsuarioLogado() throws Exception {
        UsuarioDto usuario = UsuarioDto.builder()
                .id(1L)
                .email("teste@email.com")
                .nome("teste")
                .build();

        when(usuarioService.getMe()).thenReturn(usuario);

        mockMvc.perform(
                        get("/api/usuarios/me")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("teste@email.com"))
                .andExpect(jsonPath("$.nome").value("teste"));

        verify(usuarioService).getMe();
    }
    
    @Test
    void deveEditarUsuarioLogado() throws Exception {
        UsuarioDto request = UsuarioDto.builder()
                .nome("Novo Nome")
                .email("novo@email.com")
                .build();

        AuthResponseDTO response = new AuthResponseDTO(
                "access-token",
                "refresh-token"
        );

        when(usuarioService.updateMe(any(UsuarioDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/usuarios/me")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(usuarioService).updateMe(any(UsuarioDto.class));

        
    }

}
