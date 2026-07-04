package com.projeto.amigo.secreto.service;


import com.projeto.amigo.secreto.dtos.SorteioDTO;
import com.projeto.amigo.secreto.entities.*;
import com.projeto.amigo.secreto.enums.StatusSorteio;
import com.projeto.amigo.secreto.exceptions.BusinessException;
import com.projeto.amigo.secreto.exceptions.NotFoundException;
import com.projeto.amigo.secreto.exceptions.UnauthorizedException;
import com.projeto.amigo.secreto.repositories.GrupoRepository;
import com.projeto.amigo.secreto.repositories.PessoaRepository;
import com.projeto.amigo.secreto.repositories.ResultadoSorteioRepository;
import com.projeto.amigo.secreto.repositories.SorteioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


@Service
public class SorteioService {
    private final SorteioRepository sorteioRepository;
    private final GrupoRepository grupoRepository;
    private final PessoaRepository pessoaRepository;
    private final ResultadoSorteioRepository resultadoRepository;
    private final EmailService emailService;
    public SorteioService(SorteioRepository sorteioRepository, GrupoRepository grupoRepository, PessoaRepository pessoaRepository, ResultadoSorteioRepository resultadoRepository, EmailService emailService) {
        this.sorteioRepository = sorteioRepository;
        this.grupoRepository = grupoRepository;
        this.pessoaRepository = pessoaRepository;
        this.resultadoRepository = resultadoRepository;
        this.emailService = emailService;
    }

    public SorteioDTO create(SorteioDTO dto){
        Grupo grupo = grupoRepository.findById(dto.getGrupoId())
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado"));
        dto.setStatus(StatusSorteio.EM_ANDAMENTO);
        dto.setDataSorteio(LocalDateTime.now());
        Sorteio sorteio = dto.mapToEntitie(grupo);
        sorteioRepository.save(sorteio);
        return sorteio.mapToDto();
    }

    public SorteioDTO findById(long id){
        Sorteio sorteio = sorteioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sorteio não encontrado"));

        return sorteio.mapToDto();
    }

    public List<SorteioDTO> findAll(){
        return sorteioRepository.findAll().stream().map(Sorteio::mapToDto).toList();
    }

    public void delete(long id){
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Sorteio sorteio = sorteioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sorteio não encontrado"));

        if (!sorteio.getGrupo().getDono().getId().equals(usuario.getPessoa().getId())) {
            throw new UnauthorizedException("Você não tem permissão para realizar essa ação");
        }
        sorteioRepository.delete(sorteio);
    }

    public void finalizarSorteio(Long id) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Sorteio sorteio = sorteioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sorteio não encontrado"));

        if (!sorteio.getGrupo().getDono().getId().equals(usuario.getPessoa().getId())) {
            throw new UnauthorizedException("Você não tem permissão para realizar essa ação");
        }

        if (sorteio.getStatus() == StatusSorteio.FINALIZADO) {
            throw new BusinessException("Sorteio já está finalizado");
        }
        sorteio.setStatus(StatusSorteio.FINALIZADO);

        Grupo grupo = grupoRepository.findById(sorteio.getGrupo().getId())
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado"));
        grupo.setSorteado(true);
        sorteioRepository.save(sorteio);
        grupoRepository.save(grupo);
    }

    @Transactional
    public void realizarSorteio(Long id){

        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Sorteio sorteio = sorteioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sorteio não encontrado"));

        if (!sorteio.getGrupo().getDono().getId().equals(usuario.getPessoa().getId())) {
            throw new BusinessException("Você não tem permissão para realizar essa ação");
        }

        List<Pessoa> pessoas = pessoaRepository.findAllByGrupos_Id(sorteio.getGrupo().getId());

        if (pessoas.size() < 2) {
            throw new BusinessException("É necessário pelo menos 2 pessoas no grupo para realizar o sorteio.");
        }

        if (sorteio.getStatus() == StatusSorteio.FINALIZADO) {
            throw new BusinessException("Sorteio já foi realizado.");
        }

        Collections.shuffle(pessoas);

        for(int i = 0; i < pessoas.size(); i++){
            Pessoa sorteador = pessoas.get(i);
            Pessoa sorteado = pessoas.get((i+1) % pessoas.size());

            ResultadoSorteio resultado = ResultadoSorteio.builder().sorteador(sorteador).sorteado(sorteado).sorteio(sorteio).build();
            emailService.enviarResultadoSorteio(sorteador.getEmail(), sorteador.getNome(), sorteado.getNome());
            resultadoRepository.save(resultado);
        }
    }
}
