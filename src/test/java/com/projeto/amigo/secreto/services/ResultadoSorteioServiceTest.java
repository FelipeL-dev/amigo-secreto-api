package com.projeto.amigo.secreto.services;

import com.projeto.amigo.secreto.dtos.ResultadoSorteioDTO;
import com.projeto.amigo.secreto.entities.Pessoa;
import com.projeto.amigo.secreto.entities.ResultadoSorteio;
import com.projeto.amigo.secreto.entities.Sorteio;
import com.projeto.amigo.secreto.exceptions.NotFoundException;
import com.projeto.amigo.secreto.repositories.ResultadoSorteioRepository;
import com.projeto.amigo.secreto.service.ResultadoSorteioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ResultadoSorteioServiceTest {
    @Mock
    ResultadoSorteioRepository resultadoSorteioRepository;

    @InjectMocks
    ResultadoSorteioService resultadoSorteioService;

    @Test
    void deveBuscarResultadoSorteioPorSorteio(){
        Sorteio sorteio = Sorteio.builder().id(1L).build();
        Pessoa pessoa = Pessoa.builder().id(1L).nome("teste").build();
        Pessoa pessoa2 = Pessoa.builder().id(2L).nome("teste2").build();
        ResultadoSorteio resultado = ResultadoSorteio.builder().sorteador(pessoa).sorteado(pessoa2).sorteio(sorteio).build();
        ResultadoSorteio resultado2 = ResultadoSorteio.builder().sorteado(pessoa).sorteador(pessoa2).sorteio(sorteio).build();

        when(resultadoSorteioRepository.findAllBySorteioId(1L)).thenReturn(List.of(resultado, resultado2));

        List<ResultadoSorteioDTO> response = resultadoSorteioService.findAllBySorteio(1L);

        assertEquals(2, response.size());

        assertEquals(sorteio.getId(), response.getFirst().getSorteio_id());
        assertEquals(pessoa.getId(), response.getFirst().getSorteador_id());
        assertEquals(pessoa2.getId(), response.getFirst().getSorteado_id());
        assertEquals(pessoa2.getId(), response.get(1).getSorteador_id());
        assertEquals(pessoa.getId(), response.get(1).getSorteado_id());
    }

    @Test
    void deveBuscarResultadoSorteioPorIdComSucesso(){
        Sorteio sorteio = Sorteio.builder().id(1L).build();
        Pessoa pessoa = Pessoa.builder().id(1L).nome("teste").build();
        Pessoa pessoa2 = Pessoa.builder().id(2L).nome("teste2").build();
        ResultadoSorteio resultado = ResultadoSorteio.builder().id(1L).sorteio(sorteio).sorteador(pessoa).sorteado(pessoa2).build();


        when(resultadoSorteioRepository.findById(1L)).thenReturn(Optional.of(resultado));

        ResultadoSorteioDTO response = resultadoSorteioService.findById(1L);

        assertEquals(pessoa.getId(), response.getSorteador_id());
        assertEquals(pessoa2.getId(), response.getSorteado_id());
        assertEquals(sorteio.getId(), response.getSorteio_id());
    }

    @Test
    void deveLancarNotFoundExceptionAoBuscarSorteioPorId(){
        when(resultadoSorteioRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> resultadoSorteioService.findById(1L));
    }

    @Test
    void deveDeletarResultadoSorteioComSucessso(){
        ResultadoSorteio resultadoSorteio = ResultadoSorteio.builder().id(1L).build();

        when(resultadoSorteioRepository.findById(1L)).thenReturn(Optional.of(resultadoSorteio));

        resultadoSorteioService.deleteResultadoSorteio(1L);
    }

    @Test
    void deveLancarNotFoundExceptionAoDeletarSorteio(){
        when(resultadoSorteioRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> resultadoSorteioService.deleteResultadoSorteio(1L));
    }
}
