package com.projeto.amigo.secreto.service;


import com.projeto.amigo.secreto.dtos.SorteioDTO;
import com.projeto.amigo.secreto.entities.Grupo;
import com.projeto.amigo.secreto.entities.Pessoa;
import com.projeto.amigo.secreto.entities.ResultadoSorteio;
import com.projeto.amigo.secreto.entities.Sorteio;
import com.projeto.amigo.secreto.enums.StatusSorteio;
import com.projeto.amigo.secreto.repositories.GrupoRepository;
import com.projeto.amigo.secreto.repositories.PessoaRepository;
import com.projeto.amigo.secreto.repositories.ResultadoSorteioRepository;
import com.projeto.amigo.secreto.repositories.SorteioRepository;
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

    public SorteioService(SorteioRepository sorteioRepository, GrupoRepository grupoRepository, PessoaRepository pessoaRepository, ResultadoSorteioRepository resultadoRepository) {
        this.sorteioRepository = sorteioRepository;
        this.grupoRepository = grupoRepository;
        this.pessoaRepository = pessoaRepository;
        this.resultadoRepository = resultadoRepository;
    }

    public SorteioDTO create(SorteioDTO dto){
        Grupo grupo = grupoRepository.findById(dto.getGrupoId())
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));
        dto.setStatus(StatusSorteio.EM_ANDAMENTO);
        dto.setDataSorteio(LocalDateTime.now());
        Sorteio sorteio = dto.mapToEntitie(grupo);
        sorteioRepository.save(sorteio);
        return sorteio.mapToDto();
    }

    public SorteioDTO findById(long id){
        Sorteio sorteio = sorteioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sorteio não encontrado"));

        return sorteio.mapToDto();
    }

    public List<SorteioDTO> findAll(){
        return sorteioRepository.findAll().stream().map(Sorteio::mapToDto).toList();
    }

    public void delete(long id){
        Sorteio sorteio = sorteioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sorteio não encontrado"));

        sorteioRepository.delete(sorteio);
    }

    public void finalizarSorteio(Long id) {
        Sorteio sorteio = sorteioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sorteio não encontrado"));
        if (sorteio.getStatus() == StatusSorteio.FINALIZADO) {
            throw new RuntimeException("Sorteio já está finalizado");
        }
        sorteio.setStatus(StatusSorteio.FINALIZADO);

        Grupo grupo = grupoRepository.findById(sorteio.getGrupo().getId())
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));
        grupo.setSorteado(true);
        sorteioRepository.save(sorteio);
        grupoRepository.save(grupo);
    }

    @Transactional
    public void realizarSorteio(Long id){

        Sorteio sorteio = sorteioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sorteio não encontrado"));
        List<Pessoa> pessoas = pessoaRepository.findAllByGrupos_Id(sorteio.getGrupo().getId());

        if (pessoas.size() < 2) {
            throw new RuntimeException("É necessário pelo menos 2 pessoas no grupo para realizar o sorteio.");
        }

        if (sorteio.getStatus() == StatusSorteio.FINALIZADO) {
            throw new RuntimeException("Sorteio já foi realizado.");
        }

        Collections.shuffle(pessoas);

        for(int i = 0; i < pessoas.size(); i++){
            Pessoa sorteador = pessoas.get(i);
            Pessoa sorteado = pessoas.get((i+1) % pessoas.size());

            ResultadoSorteio resultado = new ResultadoSorteio();
            resultado.setSorteador(sorteador);
            resultado.setSorteado(sorteado);
            resultado.setSorteio(sorteio);
            resultadoRepository.save(resultado);
        }
    }
}
