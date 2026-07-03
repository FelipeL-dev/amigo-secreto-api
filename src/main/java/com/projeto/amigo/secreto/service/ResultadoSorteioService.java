package com.projeto.amigo.secreto.service;

import com.projeto.amigo.secreto.dtos.ResultadoSorteioDTO;
import com.projeto.amigo.secreto.entities.Pessoa;
import com.projeto.amigo.secreto.entities.ResultadoSorteio;
import com.projeto.amigo.secreto.entities.Sorteio;
import com.projeto.amigo.secreto.exceptions.BusinessException;
import com.projeto.amigo.secreto.exceptions.NotFoundException;
import com.projeto.amigo.secreto.repositories.PessoaRepository;
import com.projeto.amigo.secreto.repositories.ResultadoSorteioRepository;
import com.projeto.amigo.secreto.repositories.SorteioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResultadoSorteioService {
    private final ResultadoSorteioRepository resultadoSorteioRepository;
    private final SorteioRepository sorteioRepository;
    private final PessoaRepository pessoaRepository;

    public ResultadoSorteioService(ResultadoSorteioRepository resultadoSorteioRepository, SorteioRepository sorteioRepository, PessoaRepository pessoaRepository) {
        this.resultadoSorteioRepository = resultadoSorteioRepository;
        this.sorteioRepository = sorteioRepository;
        this.pessoaRepository = pessoaRepository;
    }

    @Transactional
    public ResultadoSorteioDTO createResultadoSorteio(ResultadoSorteioDTO dto){
        Pessoa sorteador = pessoaRepository.findById(dto.getSorteador_id())
                .orElseThrow(() -> new NotFoundException("Sorteador não encontrado"));
        Pessoa sorteado = pessoaRepository.findById(dto.getSorteado_id())
                .orElseThrow(() -> new NotFoundException("Sorteado não encontrado"));
        Sorteio sorteio = sorteioRepository.findById(dto.getSorteio_id())
                .orElseThrow(() -> new NotFoundException("Sorteio não encontrado"));

        resultadoSorteioRepository.findBySorteadorIdAndSorteioId(sorteador.getId(), sorteio.getId())
                .ifPresent(r -> {
                    throw new BusinessException("Resultado para esse sorteador e sorteio já existe.");
                });

        ResultadoSorteio resultadoSorteio = dto.mapToEntite(sorteio, sorteador, sorteado);
        resultadoSorteioRepository.save(resultadoSorteio);
        return resultadoSorteio.mapToDto();
    }

    public List<ResultadoSorteioDTO> findAllBySorteio(Long sorteioId){
        List<ResultadoSorteio> resultados = resultadoSorteioRepository.findAllBySorteioId(sorteioId);
        return resultados.stream().map(ResultadoSorteio::mapToDto).toList();
    }

    public ResultadoSorteioDTO findById(long id){
        ResultadoSorteio resultadoSorteio = resultadoSorteioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resultado de sorteio não encontrado"));
        return resultadoSorteio.mapToDto();
    }

    public void deleteResultadoSorteio(long id){
        ResultadoSorteio resultadoSorteio = resultadoSorteioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resultado de sorteio não encontrado"));
        resultadoSorteioRepository.delete((resultadoSorteio));
    }


}
