package com.projeto.amigo.secreto.service;

import com.projeto.amigo.secreto.dtos.GrupoDTO;
import com.projeto.amigo.secreto.entities.Grupo;
import com.projeto.amigo.secreto.entities.Pessoa;
import com.projeto.amigo.secreto.entities.Usuario;
import com.projeto.amigo.secreto.exceptions.BusinessException;
import com.projeto.amigo.secreto.exceptions.NotFoundException;
import com.projeto.amigo.secreto.exceptions.UnauthorizedException;
import com.projeto.amigo.secreto.repositories.GrupoRepository;
import com.projeto.amigo.secreto.repositories.PessoaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class GrupoService {
    private final GrupoRepository grupoRepository;
    private final PessoaRepository pessoaRepository;
    public GrupoService(GrupoRepository repository, PessoaRepository pessoaRepository) {
        this.grupoRepository = repository;
        this.pessoaRepository = pessoaRepository;
    }

    public GrupoDTO create(GrupoDTO dto) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Grupo grupo = dto.mapToEntitie();
        grupo.setDataCriacao(LocalDate.now());
        grupo.setSorteado(false);
        grupo.setDono(usuario.getPessoa());
        grupoRepository.save(grupo);

        Pessoa dono = pessoaRepository.findById(usuario.getPessoa().getId()).orElseThrow(() -> new NotFoundException("Pessoa nao encontrada"));
        dono.getGrupos().add(grupo);
        pessoaRepository.save(dono);
        return grupo.mapToDto();
    }

    public List<GrupoDTO> findAll(){
        return grupoRepository.findAll().stream().map(Grupo::mapToDto).toList();
    }

    public List<GrupoDTO> findMeusGrupos(){
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pessoa pessoa = pessoaRepository.findById(usuario.getPessoa().getId()).orElseThrow(() -> new NotFoundException("Pessoa nao encontrada"));


        return pessoa.getGrupos().stream().map(Grupo::mapToDto).toList();
    }

    public GrupoDTO findById(Long id){
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado"));

        return grupo.mapToDto();
    }

    public void delete(Long id){
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado"));

        if (!grupo.getDono().getId().equals(usuario.getPessoa().getId())) {
            throw new UnauthorizedException("Você não tem permissão para realizar essa ação");
        }

        grupoRepository.delete(grupo);
    }

    public GrupoDTO update(GrupoDTO dto, Long id){

        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Grupo grupo = grupoRepository.findById(id).orElseThrow(() -> new NotFoundException("grupo nao encontrado"));

        if (!grupo.getDono().getId().equals(usuario.getPessoa().getId())) {
            throw new UnauthorizedException("Você não tem permissão para realizar essa ação");
        }

        grupo.updateGrupo(dto.getNome(), dto.getSorteado());
        grupoRepository.save(grupo);

        return grupo.mapToDto();
    }

    public List<GrupoDTO> findGruposSorteados(){
        return grupoRepository.findBySorteado(true).stream().map(Grupo::mapToDto).toList();
    }

    public String gerarConviteToken(Long id){
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Grupo grupo = grupoRepository.findById(id).orElseThrow(() -> new NotFoundException("grupo nao encontrado"));

        if (!grupo.getDono().getId().equals(usuario.getPessoa().getId())) {
            throw new UnauthorizedException("Você não tem permissão para realizar essa ação");
        }

        grupo.setTokenConvite(UUID.randomUUID().toString());
        grupoRepository.save(grupo);
        return "localhost:8080/api/grupos/entrar/" + grupo.getTokenConvite();
    }

    public void entrarNoGrupo(String token){
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Grupo grupo = grupoRepository.findByTokenConvite(token).orElseThrow(() -> new NotFoundException("Grupo nao encontrado"));

        Pessoa pessoa = pessoaRepository.findById(usuario.getPessoa().getId()).orElseThrow(() -> new NotFoundException("Pessoa nao encontrada"));



        if(pessoa.getGrupos().contains(grupo)){
            throw new BusinessException("Você já faz parte desse grupo");
        }

        pessoa.getGrupos().add(grupo);
        pessoaRepository.save(pessoa);

    }

}
