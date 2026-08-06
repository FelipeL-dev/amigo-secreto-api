package com.projeto.amigo.secreto.services;

import com.projeto.amigo.secreto.dtos.PessoaDTO;
import com.projeto.amigo.secreto.entities.Grupo;
import com.projeto.amigo.secreto.entities.Pessoa;
import com.projeto.amigo.secreto.exceptions.NotFoundException;
import com.projeto.amigo.secreto.repositories.GrupoRepository;
import com.projeto.amigo.secreto.repositories.PessoaRepository;
import com.projeto.amigo.secreto.service.PessoaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PessoaServiceTest {
    @Mock
    PessoaRepository pessoaRepository;

    @Mock
    GrupoRepository grupoRepository;

    @InjectMocks
    PessoaService pessoaService;

    @Test
    void deveRetornarTodasAsPessoas(){
        Pessoa pessoa1 = Pessoa.builder().nome("teste").id(1L).build();
        Pessoa pessoa2 = Pessoa.builder().nome("teste2").id(2L).build();

        when(pessoaRepository.findAll()).thenReturn(List.of(pessoa1, pessoa2));

        List<PessoaDTO> pessoas = pessoaService.findAll();

        assertEquals(2, pessoas.size());
        assertEquals("teste", pessoas.get(0).getNome());
        assertEquals("teste2", pessoas.get(1).getNome());
        verify(pessoaRepository).findAll();
    }

    @Test
    void deveRetornarPessoaPorId(){
        Pessoa pessoa = Pessoa.builder().id(1L).nome("teste").build();

        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));

        PessoaDTO dto = pessoaService.findById(1L);

        assertEquals("teste", dto.getNome());
        verify(pessoaRepository).findById(1L);
    }

    @Test
    void deveRetornarNotFoundExceptionAoBuscarPessoaPorId(){
        when(pessoaRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pessoaService.findById(1L));
    }

    @Test
    void deveDeletarPessoaComSucesso(){
        Pessoa pessoa = Pessoa.builder().nome("teste").id(1L).build();

        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
    }

    @Test
    void deveLancarNotFoundExceptionAoDeletarPessoa(){
        when(pessoaRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pessoaService.delete(any()));
    }

    @Test
    void deveEditarPessoaComSucesso(){
        Pessoa pessoa = Pessoa.builder().id(1L).nome("teste").email("teste@email.com").build();

        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));

        PessoaDTO dto = new PessoaDTO();
        dto.setNome("teste2");
        dto.setEmail("teste2@email.com");
        pessoaService.update(dto, 1L);

        verify(pessoaRepository).save(pessoa);
        assertEquals("teste2", pessoa.getNome());
        assertEquals("teste2@email.com", pessoa.getEmail());
    }

    @Test
    void deveLancarNotFoundExceptionAoAtualizarPessoa(){
        PessoaDTO dto = new PessoaDTO();
        when(pessoaRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pessoaService.update(dto, any()));
    }

    @Test
    void deveBuscarPessoasPorGrupoId(){
        Pessoa pessoa1 = Pessoa.builder().id(1L).nome("pessoa1").build();
        Pessoa pessoa2 = Pessoa.builder().id(2L).nome("pessoa2").build();

        when(pessoaRepository.findAllByGrupos_Id(1L)).thenReturn(List.of(pessoa1, pessoa2));

        List<PessoaDTO> response = pessoaService.findByGrupoId(1L);

        assertEquals(2, response.size());
        assertEquals("pessoa1", response.get(0).getNome());
        assertEquals("pessoa2", response.get(1).getNome());
    }
    @Test
    void deveLancarNotFoundExceptionAoBuscarPessoaPorGrupo(){
        when(pessoaRepository.findAllByGrupos_Id(any())).thenReturn(new ArrayList<>());

        assertThrows(NotFoundException.class, () -> pessoaService.findByGrupoId(any()));
    }

    @Test
    void deveAdicionarPessoaAGrupoComSucesso(){
        Pessoa pessoa = Pessoa.builder().id(1L).nome("teste").grupos(new ArrayList<>()).build();
        Grupo grupo = Grupo.builder().nome("grupo").pessoas(new ArrayList<>()).id(1L).build();

        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));


        pessoaService.adicionarPessoaAGrupo(1L, 1L);
        verify(grupoRepository).save(grupo);
        verify(pessoaRepository).save(pessoa);
        assertEquals("grupo", pessoa.getGrupos().getFirst().getNome());
        assertEquals("teste", grupo.getPessoas().getFirst().getNome());
    }

    @Test
    void deveRetornarNotFoundExceptionPessoaAoAdicionarPessoaGrupo(){

        when(pessoaRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> pessoaService.adicionarPessoaAGrupo(1L, 1L));
    }
}
