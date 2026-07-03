package com.projeto.amigo.secreto.service;

import com.projeto.amigo.secreto.dtos.GrupoDTO;
import com.projeto.amigo.secreto.entities.Grupo;
import com.projeto.amigo.secreto.entities.Pessoa;
import com.projeto.amigo.secreto.entities.Usuario;
import com.projeto.amigo.secreto.repositories.GrupoRepository;
import com.projeto.amigo.secreto.repositories.PessoaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

        Pessoa dono = pessoaRepository.findById(usuario.getPessoa().getId()).orElseThrow(() -> new RuntimeException("Pessoa nao encontrada"));
        dono.getGrupos().add(grupo);
        pessoaRepository.save(dono);
        return grupo.mapToDto();
    }

    public List<GrupoDTO> findAll(){
        return grupoRepository.findAll().stream().map(Grupo::mapToDto).toList();
    }

    public List<GrupoDTO> findMeusGrupos(){
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pessoa pessoa = pessoaRepository.findById(usuario.getPessoa().getId()).orElseThrow(() -> new RuntimeException("Pessoa nao encontrada"));


        return pessoa.getGrupos().stream().map(Grupo::mapToDto).toList();
    }

    public GrupoDTO findById(Long id){
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));

        return grupo.mapToDto();
    }

    public void delete(Long id){
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));

        if (!grupo.getDono().getId().equals(usuario.getPessoa().getId())) {
            throw new RuntimeException("Você não tem permissão para realizar essa ação");
        }

        grupoRepository.delete(grupo);
    }

    public GrupoDTO update(GrupoDTO dto, Long id){

        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Grupo> optionalGrupo = grupoRepository.findById(id);

        if(optionalGrupo.isEmpty()){
            throw new RuntimeException("Grupo não encontrado");
        }

        if (!optionalGrupo.get().getDono().getId().equals(usuario.getPessoa().getId())) {
            throw new RuntimeException("Você não tem permissão para realizar essa ação");
        }

        Grupo grupo = optionalGrupo.get();
        grupo.updateGrupo(dto.getNome(), dto.getSorteado());
        grupoRepository.save(grupo);

        return grupo.mapToDto();
    }

    public List<GrupoDTO> findGruposSorteados(){
        return grupoRepository.findBySorteado(true).stream().map(Grupo::mapToDto).toList();
    }

}
