package com.projeto.amigo.secreto.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.amigo.secreto.dtos.*;
import com.projeto.amigo.secreto.security.JwtService;
import com.projeto.amigo.secreto.service.AuthService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void deveRegistrarUsuario() throws Exception {

        RegisterRequestDTO request = new RegisterRequestDTO(
                "teste@gmail.com",
                "123456",
                "Teste"
        );

        AuthResponseDTO response = new AuthResponseDTO(
                "access-token",
                "refresh-token"
        );

        when(authService.register(any(RegisterRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/auth/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

        verify(authService).register(any(RegisterRequestDTO.class));
    }

    @Test
    void deveRealizarLogin() throws Exception {
        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder().email("teste@email.com").password("123456").build();

        AuthResponseDTO response = new AuthResponseDTO(
                "access-token",
                "refresh-token"
        );

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(response);

        mockMvc.perform(
                post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"));
        verify(authService).login(loginRequestDTO);
    }

    @Test
    void deveRenovarAccessToken() throws Exception {
        RefreshRequestDto refreshRequestDto = RefreshRequestDto.builder().refreshToken("access-token").build();

        AuthResponseDTO response = new AuthResponseDTO(
                "access-token",
                "refresh-token"
        );

        when(authService.refresh(any(RefreshRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/auth/refresh").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(refreshRequestDto))).andExpect(status().isOk()).andExpect(jsonPath("$.token").value("access-token"));
    }

    @Test
    void deveVerificarEmail() throws Exception {
        VerificarEmailDto verificarEmailDto = VerificarEmailDto.builder().codigo("teste123").build();

       mockMvc.perform(post("/auth/verificar").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(verificarEmailDto))).andExpect(status().isOk());

    }

    @Test
    void deveReenviarEmail() throws Exception {
        ReenviarEmailDto reenviarEmailDto =  ReenviarEmailDto.builder().email("teste@email.com").build();
        mockMvc.perform(post("/auth/reenviar-verificacao").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(reenviarEmailDto))).andExpect(status().isOk());
    }

    @Test
    void deveExecutarEsqueceuSenha() throws Exception {
        ReenviarEmailDto reenviarEmailDto = ReenviarEmailDto.builder().email("teste@email.com").build();

        mockMvc.perform(post("/auth/esqueceu-senha").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(reenviarEmailDto))).andExpect(status().isOk());
    }

    @Test
    void deveRedefinirSenha() throws Exception{
        RedefinirSenhaDTO redefinirSenhaDTO = RedefinirSenhaDTO.builder().senha("123456").codigo("123456").email("teste@email.com").build();

        mockMvc.perform(post("/auth/redefinir-senha").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(redefinirSenhaDTO))).andExpect(status().isOk());
    }
}