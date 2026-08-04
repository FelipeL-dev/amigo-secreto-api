package com.projeto.amigo.secreto.services;

import com.projeto.amigo.secreto.dtos.AuthResponseDTO;
import com.projeto.amigo.secreto.dtos.LoginRequestDTO;
import com.projeto.amigo.secreto.dtos.RefreshRequestDto;
import com.projeto.amigo.secreto.dtos.RegisterRequestDTO;
import com.projeto.amigo.secreto.entities.Pessoa;
import com.projeto.amigo.secreto.entities.RefreshToken;
import com.projeto.amigo.secreto.entities.Usuario;
import com.projeto.amigo.secreto.enums.Role;
import com.projeto.amigo.secreto.exceptions.BusinessException;
import com.projeto.amigo.secreto.exceptions.NotFoundException;
import com.projeto.amigo.secreto.repositories.PessoaRepository;
import com.projeto.amigo.secreto.repositories.UsuarioRepository;
import com.projeto.amigo.secreto.security.JwtService;
import com.projeto.amigo.secreto.service.AuthService;
import com.projeto.amigo.secreto.service.EmailService;
import com.projeto.amigo.secreto.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @Mock
    private
    AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveEnviarCodigoDeRecSenha() {
        Pessoa pessoa = Pessoa.builder()
                .nome("Felipe")
                .build();

        Usuario usuario = Usuario.builder()
                .email("teste@gmail.com")
                .pessoa(pessoa)
                .build();

        when(usuarioRepository.findByEmail("teste@gmail.com"))
                .thenReturn(Optional.of(usuario));

        authService.enviarCodigoRecSenha("teste@gmail.com");

        verify(usuarioRepository).save(usuario);
        verify(emailService).enviarCodigoRecSenha(
                eq("teste@gmail.com"),
                eq("Felipe"),
                anyString()
        );
    }

    @Test
    void deveLancarErroUsuarioNaoEncontradoAoEnviarCodigoRecSenha(){
        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.enviarCodigoRecSenha("teste@email.com"));
    }

    @Test
    void deveVerificarEmailComSucesso() {
        // Arrange
        Usuario usuario = Usuario.builder()
                .email("teste@gmail.com")
                .emailVerificado(false)
                .codigoVerificacao("123456")
                .codigoVerificacaoExpiracao(LocalDateTime.now().plusMinutes(10))
                .build();

        when(usuarioRepository.findByCodigoVerificacao("123456"))
                .thenReturn(Optional.of(usuario));

        // Act
        authService.verificarEmail("123456");

        // Assert
        assertTrue(usuario.getEmailVerificado());
        assertNull(usuario.getCodigoVerificacao());
        assertNull(usuario.getCodigoVerificacaoExpiracao());

        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoCodigoVerificacaoNaoExiste() {

        when(usuarioRepository.findByCodigoVerificacao("123456"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> authService.verificarEmail("123456"));

    }

    @Test
    void deveLancarExcecaoQuandoCodigoVerificacaoExpirado() {

        Usuario usuario = Usuario.builder()
                .codigoVerificacao("123456")
                .codigoVerificacaoExpiracao(LocalDateTime.now().minusMinutes(1))
                .build();

        when(usuarioRepository.findByCodigoVerificacao("123456"))
                .thenReturn(Optional.of(usuario));

        assertThrows(BusinessException.class,
                () -> authService.verificarEmail("123456"));

    }

    @Test
    void deveReenviarCodigoVerificacao() {

        Pessoa pessoa = Pessoa.builder()
                .nome("Felipe")
                .build();

        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .emailVerificado(false)
                .pessoa(pessoa)
                .role(Role.ROLE_USER)
                .build();

        when(usuarioRepository.findByEmail("teste@email.com"))
                .thenReturn(Optional.of(usuario));

        authService.reenviarCodigoVerificacao("teste@email.com");

        assertNotNull(usuario.getCodigoVerificacao());

        assertNotNull(usuario.getCodigoVerificacaoExpiracao());

        verify(usuarioRepository).save(usuario);

        verify(emailService).enviarCodigoVerificacao(
                eq("teste@email.com"),
                eq("Felipe"),
                anyString()
        );
    }

    @Test
    void deveLancarExcecaoUsuarioNaoEncontradoAoReenviarCodigoVerificacao() {
        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.reenviarCodigoVerificacao("teste@email.com"));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaEstiverVerificado() {
        Usuario usuario = Usuario.builder().email("teste@email.com").emailVerificado(true).build();

        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        assertThrows(BusinessException.class, () -> authService.reenviarCodigoVerificacao("teste@email.com"));
    }

    @Test
    void deveRedefinirSenha() {
        Usuario usuario = Usuario.builder().email("teste@email.com").codigoRedefinicaoSenha("123456").codigoRedefinicaoExpiracao(LocalDateTime.now().plusMinutes(10)).build();

        when(usuarioRepository.findByEmailAndCodigoRedefinicaoSenha("teste@email.com", "123456")).thenReturn(Optional.of(usuario));

        when(passwordEncoder.encode("123456")).thenReturn("senhaCriptografada");

        authService.redefinirSenha("123456", "teste@email.com", "123456");

        assertEquals("senhaCriptografada", usuario.getPassword());
        assertNull(usuario.getCodigoRedefinicaoExpiracao());
        assertNull(usuario.getCodigoRedefinicaoSenha());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoCodigoRecSenhaForInvalido(){
        when(usuarioRepository.findByEmailAndCodigoRedefinicaoSenha("teste@email.com", "123456")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.redefinirSenha("123456", "teste@email.com", "123456"));
    }

    @Test
    void deveLancarExcecaoQuandoCodigoRecSenhaEstiverExpirado(){
        Usuario usuario = Usuario.builder().email("teste@email.com").codigoRedefinicaoSenha("123456").codigoRedefinicaoExpiracao(LocalDateTime.now().minusMinutes(1)).build();

        when(usuarioRepository.findByEmailAndCodigoRedefinicaoSenha(usuario.getEmail(), usuario.getCodigoRedefinicaoSenha())).thenReturn(Optional.of(usuario));

        assertThrows(BusinessException.class, () -> authService.redefinirSenha("123456", "teste@email.com", "123456"));
    }

    @Test
    void deveRegistrarUsuario() {
        RegisterRequestDTO dto = new RegisterRequestDTO("teste@email.com", "123456", "teste");
        when(passwordEncoder.encode("123456")).thenReturn("senhaCriptografada");
        when(jwtService.generateToken(any())).thenReturn("access-token");

        RefreshToken refresh = RefreshToken.builder().token("refresh-token").build();
        when(refreshTokenService.createRefreshToken(any())).thenReturn(refresh);

        AuthResponseDTO response = authService.register(dto);
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("access-token", response.getToken());
        verify(pessoaRepository).save(any(Pessoa.class));
        verify(usuarioRepository).save(any(Usuario.class));
        verify(emailService).enviarCodigoVerificacao(eq("teste@email.com"), eq("teste"), anyString());
        verify(jwtService).generateToken(any(Usuario.class));
        verify(refreshTokenService).createRefreshToken(any(Usuario.class));
    }

    @Test
    void deveLogarComSucesso() {
        LoginRequestDTO dto = new LoginRequestDTO("teste@email.com", "123456");
        Usuario usuario = Usuario.builder().email("teste@email.com").emailVerificado(true).build();
        when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(usuario)).thenReturn("access-token");
        RefreshToken refresh = RefreshToken.builder().token("refresh-token").build();
        when(refreshTokenService.createRefreshToken(usuario)).thenReturn(refresh);

        AuthResponseDTO response = authService.login(dto);

        assertEquals("access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(usuario);
        verify(refreshTokenService).createRefreshToken(usuario);
    }

    @Test
    void deveLancarExcecaoLoginUsuarioNaoEncontrado(){
        LoginRequestDTO dto = new LoginRequestDTO("teste@email.com", "123456");
        when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.login(dto));
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoVerificadoLogin(){
        LoginRequestDTO dto = new LoginRequestDTO("teste@email.com", "123456");
        Usuario usuario = Usuario.builder().email("teste@email.com").emailVerificado(false).build();

        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(usuario));

        assertThrows(BusinessException.class, () -> authService.login(dto));
    }

    @Test
    void deveFazerRefresh(){
        RefreshRequestDto refresh = new RefreshRequestDto("refresh-token");
        RefreshToken token = RefreshToken.builder().token(refresh.getRefreshToken()).usuario(Usuario.builder().build()).build();
        when(refreshTokenService.validateRefreshToken("refresh-token")).thenReturn(token);
        when(jwtService.generateToken(token.getUsuario())).thenReturn("novo-token");

        AuthResponseDTO response = authService.refresh(refresh);
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("novo-token", response.getToken());
        verify(refreshTokenService).validateRefreshToken("refresh-token");
        verify(jwtService).generateToken(token.getUsuario());
    }
}