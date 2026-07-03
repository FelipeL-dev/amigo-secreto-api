package com.projeto.amigo.secreto.service;

import com.projeto.amigo.secreto.dtos.AuthResponseDTO;
import com.projeto.amigo.secreto.dtos.LoginRequestDTO;
import com.projeto.amigo.secreto.dtos.RefreshRequestDto;
import com.projeto.amigo.secreto.dtos.RegisterRequestDTO;
import com.projeto.amigo.secreto.entities.Pessoa;
import com.projeto.amigo.secreto.entities.RefreshToken;
import com.projeto.amigo.secreto.entities.Usuario;
import com.projeto.amigo.secreto.enums.Role;
import com.projeto.amigo.secreto.repositories.PessoaRepository;
import com.projeto.amigo.secreto.repositories.UsuarioRepository;
import com.projeto.amigo.secreto.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PessoaRepository pessoaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        Pessoa pessoa = Pessoa.builder().nome(request.getNome()).email(request.getEmail()).build();
        pessoaRepository.save(pessoa);

        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .pessoa(pessoa)
                .build();
        usuarioRepository.save(usuario);
        String accessToken = jwtService.generateToken(usuario);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario);
        return new AuthResponseDTO(accessToken, refreshToken.getToken());
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("email nao encontrado"));

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
}