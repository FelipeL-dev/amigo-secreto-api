package com.projeto.amigo.secreto.service;


import com.projeto.amigo.secreto.dtos.PessoaDTO;
import com.projeto.amigo.secreto.entities.Grupo;
import com.projeto.amigo.secreto.entities.Pessoa;
import com.projeto.amigo.secreto.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import com.projeto.amigo.secreto.repositories.PessoaRepository;
import com.projeto.amigo.secreto.repositories.GrupoRepository;

import java.util.List;


@Service
public class PessoaService {
    private final GrupoRepository grupoRepository;
    private final PessoaRepository pessoaRepository;

    public PessoaService(GrupoRepository grupoRepository, PessoaRepository repository){
        this.grupoRepository = grupoRepository;
        this.pessoaRepository = repository;
    }

    public List<PessoaDTO> findAll(){
        return pessoaRepository.findAll().stream().map(Pessoa::mapToDto).toList();
    }

    public PessoaDTO findById(Long id){
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pessoa não encontrada"));

        return pessoa.mapToDto();
    }

    public void delete(Long id){
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pessoa não encontrada"));
        pessoaRepository.delete(pessoa);
    }

    public PessoaDTO update(PessoaDTO dto, Long id){
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pessoa não encontrada"));

        pessoa.setNome(dto.getNome());
        pessoa.setEmail(dto.getEmail());

        if (dto.getGrupoIds() != null && !dto.getGrupoIds().isEmpty()) {
            List<Grupo> grupos = grupoRepository.findAllById(dto.getGrupoIds());
            pessoa.setGrupos(grupos);
        }

        pessoaRepository.save(pessoa);
        return pessoa.mapToDto();
    }

    public List<PessoaDTO> findByGrupoId(Long grupoId) {
        List<Pessoa> pessoas = pessoaRepository.findAllByGrupos_Id(grupoId);
        if (pessoas.isEmpty()) {
            throw new NotFoundException("Grupo não encontrado ou sem pessoas cadastradas");
        }
        return pessoas.stream().map(Pessoa::mapToDto).toList();
    }

    public void adicionarPessoaAGrupo(Long grupoId, Long pessoaId){
        Pessoa pessoa = pessoaRepository.findById(pessoaId).orElseThrow(() -> new NotFoundException("Pessoa não encontrada"));
        Grupo grupo = grupoRepository.findById(grupoId).orElseThrow(()-> new NotFoundException(("Grupo não encontrado")));

        if (!pessoa.getGrupos().contains(grupo)){
            pessoa.getGrupos().add(grupo);
        }

        if(!grupo.getPessoas().contains(pessoa)){
            grupo.getPessoas().add(pessoa);
        }

        pessoaRepository.save(pessoa);
        grupoRepository.save(grupo);
    }
}
