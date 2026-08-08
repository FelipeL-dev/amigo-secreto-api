package com.projeto.amigo.secreto.services;

import com.projeto.amigo.secreto.dtos.AuthResponseDTO;
import com.projeto.amigo.secreto.dtos.UsuarioDto;
import com.projeto.amigo.secreto.entities.Pessoa;
import com.projeto.amigo.secreto.entities.RefreshToken;
import com.projeto.amigo.secreto.entities.Usuario;
import com.projeto.amigo.secreto.exceptions.NotFoundException;
import com.projeto.amigo.secreto.repositories.PessoaRepository;
import com.projeto.amigo.secreto.repositories.UsuarioRepository;
import com.projeto.amigo.secreto.security.JwtService;
import com.projeto.amigo.secreto.service.RefreshTokenService;
import com.projeto.amigo.secreto.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {
    @Mock
    JwtService jwtService;

    @Mock
    RefreshTokenService refreshTokenService;

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    PessoaRepository pessoaRepository;

    @InjectMocks
    UsuarioService usuarioService;

    private void mockUsuarioAutenticado(Usuario usuario) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getPrincipal()).thenReturn(usuario);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void deveEncontrarUsuarioLogado(){
        Pessoa pessoa = Pessoa.builder().id(1L).build();
        Usuario usuario = Usuario.builder().Id(1L).email("teste@email.com").pessoa(pessoa).build();
        mockUsuarioAutenticado(usuario);

        usuarioService.getMe();
    }

    @Test
    void deveAtualizarUsuarioLogadoComSucesso(){
        Pessoa pessoa = Pessoa.builder().id(1L).nome("nome antigo").build();
        Usuario usuario = Usuario.builder().Id(1L).email("antigo@email.com").pessoa(pessoa).build();
        mockUsuarioAutenticado(usuario);
        UsuarioDto dto = new UsuarioDto();
        dto.setNome("nome novo");
        dto.setEmail("novo@email.com");

        when(usuarioRepository.findByEmail("antigo@email.com")).thenReturn(Optional.of(usuario));
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));

        RefreshToken refreshToken = RefreshToken.builder().usuario(usuario).token("refresh-token").build();

        when(jwtService.generateToken(usuario)).thenReturn("novo-access-token");

        when(refreshTokenService.createRefreshToken(usuario)).thenReturn(refreshToken);

        AuthResponseDTO response = usuarioService.updateMe(dto);

        assertEquals("novo-access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("nome novo", pessoa.getNome());
        assertEquals("novo@email.com", pessoa.getEmail());
        assertEquals("novo@email.com", usuario.getEmail());

        verify(pessoaRepository).save(pessoa);
        verify(usuarioRepository).save(usuario);
        verify(jwtService).generateToken(usuario);
        verify(refreshTokenService).createRefreshToken(usuario);
    }

    @Test
    void deveLancarNotFoundExceptionUsuarioAoAtualizarUsuario(){
        Pessoa pessoa = Pessoa.builder().id(1L).nome("nome antigo").build();
        Usuario usuario = Usuario.builder().Id(1L).email("antigo@email.com").pessoa(pessoa).build();
        mockUsuarioAutenticado(usuario);
        UsuarioDto dto = new UsuarioDto();
        dto.setNome("nome novo");
        dto.setEmail("novo@email.com");

        when(usuarioRepository.findByEmail("antigo@email.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> usuarioService.updateMe(dto));
    }

    @Test
    void deveLancarNotFoundExceptionPessoaAoAtualizarUsuario(){
        Pessoa pessoa = Pessoa.builder().id(1L).nome("nome antigo").build();
        Usuario usuario = Usuario.builder().Id(1L).email("antigo@email.com").pessoa(pessoa).build();
        mockUsuarioAutenticado(usuario);
        UsuarioDto dto = new UsuarioDto();
        dto.setNome("nome novo");
        dto.setEmail("novo@email.com");


        assertThrows(NotFoundException.class, () -> usuarioService.updateMe(dto));
    }
}
