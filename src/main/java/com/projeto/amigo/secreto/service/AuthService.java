package com.projeto.amigo.secreto.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PessoaRepository pessoaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        Pessoa pessoa = Pessoa.builder().nome(request.getNome()).email(request.getEmail()).build();
        pessoaRepository.save(pessoa);

        String codigo = String.format("%06d", new Random().nextInt(999999));
        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .codigoVerificacao(codigo)
                .codigoVerificacaoExpiracao(LocalDateTime.now().plusMinutes(10))
                .emailVerificado(false)
                .pessoa(pessoa)
                .build();
        usuarioRepository.save(usuario);

        emailService.enviarCodigoVerificacao(request.getEmail(), request.getNome(), codigo);

        String accessToken = jwtService.generateToken(usuario);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario);

        return new AuthResponseDTO(accessToken, refreshToken.getToken());
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("email nao encontrado"));

        if(!usuario.getEmailVerificado()){
            throw new BusinessException("Email nao verificado. Verifique seu email antes de realizar o login");
        }

        String accessToken = jwtService.generateToken(usuario);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario);

        return new AuthResponseDTO(accessToken, refreshToken.getToken());
    }

    public AuthResponseDTO refresh(RefreshRequestDto request){
        RefreshToken refreshToken= refreshTokenService.validateRefreshToken(request.getRefreshToken());
        Usuario usuario = refreshToken.getUsuario();
        String newAccessToken = jwtService.generateToken(usuario);

        return new AuthResponseDTO(newAccessToken, refreshToken.getToken());
    }

    public void verificarEmail(String codigo){
        Usuario usuario = usuarioRepository.findByCodigoVerificacao(codigo).orElseThrow(() -> new NotFoundException("Codigo invalido"));

        if(usuario.getCodigoVerificacaoExpiracao().isBefore(LocalDateTime.now())){
            throw new BusinessException("Codigo de verificacao expirado, solicite um novo");
        }

        usuario.setEmailVerificado(true);
        usuario.setCodigoVerificacao(null);
        usuario.setCodigoVerificacaoExpiracao(null);
        usuarioRepository.save(usuario);
    }

    public void reenviarCodigoVerificacao(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (usuario.getEmailVerificado()) {
            throw new BusinessException("Email já verificado.");
        }

        String codigo = String.format("%06d", new Random().nextInt(999999));
        usuario.setCodigoVerificacao(codigo);
        usuario.setCodigoVerificacaoExpiracao(LocalDateTime.now().plusMinutes(10));
        usuarioRepository.save(usuario);

        emailService.enviarCodigoVerificacao(email, usuario.getPessoa().getNome(), codigo);
    }
}