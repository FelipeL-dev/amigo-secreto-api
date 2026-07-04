package com.projeto.amigo.secreto.service;

import com.projeto.amigo.secreto.dtos.AuthResponseDTO;
import com.projeto.amigo.secreto.dtos.UsuarioDto;
import com.projeto.amigo.secreto.entities.Pessoa;
import com.projeto.amigo.secreto.entities.RefreshToken;
import com.projeto.amigo.secreto.entities.Usuario;
import com.projeto.amigo.secreto.exceptions.NotFoundException;
import com.projeto.amigo.secreto.repositories.PessoaRepository;
import com.projeto.amigo.secreto.repositories.UsuarioRepository;
import com.projeto.amigo.secreto.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UsuarioRepository usuarioRepository;
    private final PessoaRepository pessoaRepository;

    public UsuarioDto getMe() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();



        return new UsuarioDto(
                usuario.getId(),
                usuario.getPessoa().getNome(),
                usuario.getEmail()
        );
    }

    public AuthResponseDTO updateMe(UsuarioDto dto) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Usuario usuarioCompleto = usuarioRepository.findByEmail(usuario.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        Pessoa pessoa = pessoaRepository.findById(usuarioCompleto.getPessoa().getId())
                .orElseThrow(() -> new NotFoundException("Pessoa não encontrada"));

        pessoa.setNome(dto.getNome());
        pessoa.setEmail(dto.getEmail());
        usuarioCompleto.setEmail(dto.getEmail());

        pessoaRepository.save(pessoa);
        usuarioRepository.save(usuarioCompleto);
        String newAccessToken = jwtService.generateToken(usuarioCompleto);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuarioCompleto);

        return new AuthResponseDTO(newAccessToken, refreshToken.getToken());
    }
}