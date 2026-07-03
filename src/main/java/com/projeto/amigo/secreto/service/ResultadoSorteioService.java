package com.projeto.amigo.secreto.service;

import com.projeto.amigo.secreto.dtos.ResultadoSorteioDTO;
import com.projeto.amigo.secreto.entities.ResultadoSorteio;
import com.projeto.amigo.secreto.exceptions.NotFoundException;
import com.projeto.amigo.secreto.repositories.PessoaRepository;
import com.projeto.amigo.secreto.repositories.ResultadoSorteioRepository;
import com.projeto.amigo.secreto.repositories.SorteioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultadoSorteioService {
    private final ResultadoSorteioRepository resultadoSorteioRepository;


    public ResultadoSorteioService(ResultadoSorteioRepository resultadoSorteioRepository, SorteioRepository sorteioRepository, PessoaRepository pessoaRepository) {
        this.resultadoSorteioRepository = resultadoSorteioRepository;
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
