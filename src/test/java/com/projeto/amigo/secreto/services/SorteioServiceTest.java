package com.projeto.amigo.secreto.services;

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
import com.projeto.amigo.secreto.service.EmailService;
import com.projeto.amigo.secreto.service.SorteioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class SorteioServiceTest {
    @Mock
    SorteioRepository sorteioRepository;

    @Mock
    GrupoRepository grupoRepository;

    @Mock
    PessoaRepository pessoaRepository;

    @Mock
    ResultadoSorteioRepository resultadoSorteioRepository;

    @Mock
    EmailService emailService;

    @InjectMocks
    SorteioService sorteioService;

    private void mockUsuarioAutenticado(Usuario usuario) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getPrincipal()).thenReturn(usuario);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void deveCriarSorteioComSucesso() {

        Grupo grupo = Grupo.builder()
                .id(1L)
                .nome("teste")
                .build();

        SorteioDTO dto = SorteioDTO.builder()
                .grupoId(1L)
                .build();

        when(grupoRepository.findById(1L))
                .thenReturn(Optional.of(grupo));

        when(sorteioRepository.save(any(Sorteio.class)))
                .thenAnswer(invocation -> {
                    Sorteio sorteio = invocation.getArgument(0);
                    sorteio.setId(1L);
                    return sorteio;
                });

        SorteioDTO response = sorteioService.create(dto);

        assertEquals(1L, response.getId());
        assertEquals(1L, response.getGrupoId());
        assertEquals(StatusSorteio.EM_ANDAMENTO, response.getStatus());
        assertNotNull(response.getDataSorteio());

        verify(grupoRepository).findById(1L);
        verify(sorteioRepository).save(any(Sorteio.class));
    }

    @Test
    void deveLancarNotFoundExceptionAoCriarSorteio(){
        SorteioDTO dto = SorteioDTO.builder().build();
        when(grupoRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sorteioService.create(dto));
    }

    @Test
    void deveBuscarSorteioPorId(){
        Sorteio sorteio = Sorteio.builder().dataSorteio(LocalDateTime.now()).status(StatusSorteio.EM_ANDAMENTO).id(1L).build();
        when(sorteioRepository.findById(1L)).thenReturn(Optional.of(sorteio));

        SorteioDTO response = sorteioService.findById(1L);

        assertEquals(sorteio.getDataSorteio(), response.getDataSorteio());
        assertEquals(sorteio.getStatus(), response.getStatus());
    }

    @Test
    void deveLancarNotFoundExceptionAoBuscarSorteio(){
        assertThrows(NotFoundException.class, () -> sorteioService.findById(1L));
    }

    @Test
    void deveBuscarTodosOsSorteios(){
        Sorteio sorteio = Sorteio.builder()
                .id(1L)
                .dataSorteio(LocalDateTime.now())
                .status(StatusSorteio.FINALIZADO)
                .build();
        Sorteio sorteio2 = Sorteio.builder()
                .id(2L)
                .dataSorteio(LocalDateTime.now().minusDays(1))
                .status(StatusSorteio.EM_ANDAMENTO)
                .build();

        when(sorteioRepository.findAll()).thenReturn(List.of(sorteio, sorteio2));

        List<SorteioDTO> response = sorteioService.findAll();

        assertEquals(2, response.size());
        assertEquals(sorteio.getStatus(), response.getFirst().getStatus());
        assertEquals(sorteio.getDataSorteio(), response.getFirst().getDataSorteio());
        assertEquals(sorteio2.getStatus(), response.get(1).getStatus());
        assertEquals(sorteio2.getDataSorteio(), response.get(1).getDataSorteio());
    }

    @Test
    void deveDeletarSorteioComSucesso(){
        Grupo grupo = Grupo.builder()
                .id(1L)
                .build();
        Sorteio sorteio = Sorteio.builder()
                .id(1L)
                .grupo(grupo)
                .build();
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .grupos(List.of(grupo))
                .build();
        grupo.setDono(pessoa);
        Usuario usuario = Usuario.builder()
                .Id(1L)
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();

        mockUsuarioAutenticado(usuario);

        when(sorteioRepository.findById(1L)).thenReturn(Optional.of(sorteio));

        sorteioService.delete(1L);
    }

    @Test
    void deveLancarNotFoundExceptionAoDeletarSorteio(){
        Grupo grupo = Grupo.builder()
                .id(1L)
                .build();
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .grupos(List.of(grupo))
                .build();
        grupo.setDono(pessoa);
        Usuario usuario = Usuario.builder()
                .Id(1L)
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();
        mockUsuarioAutenticado(usuario);
        when(sorteioRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> sorteioService.delete(1L));
    }

    @Test
    void deveLancarBusinessExceptionAODeletarSorteio(){
        Grupo grupo = Grupo.builder()
                .id(1L)
                .build();
        Sorteio sorteio = Sorteio.builder()
                .id(1L)
                .grupo(grupo)
                .build();
        Pessoa dono = Pessoa.builder()
                .id(2L)
                .grupos(List.of(grupo))
                .build();
        grupo.setDono(dono);
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .build();
        Usuario usuario = Usuario.builder()
                .Id(1L)
                .email("teste@email.com")
                .pessoa(pessoa).build();

        mockUsuarioAutenticado(usuario);

        when(sorteioRepository.findById(1L)).thenReturn(Optional.of(sorteio));

        assertThrows(UnauthorizedException.class, ()-> sorteioService.delete(1L));
    }

    @Test
    void deveFinalizarSorteioComSucesso(){
        Grupo grupo = Grupo.builder()
                .id(1L)
                .sorteado(true)
                .build();
        Sorteio sorteio = Sorteio.builder()
                .id(1L).grupo(grupo)
                .status(StatusSorteio.EM_ANDAMENTO)
                .build();
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .grupos(List.of(grupo))
                .build();
        grupo.setDono(pessoa);
        Usuario usuario = Usuario.builder()
                .Id(1L)
                .email("teste@email.com")
                .pessoa(pessoa).build();

        mockUsuarioAutenticado(usuario);

        when(sorteioRepository.findById(1L)).thenReturn(Optional.of(sorteio));
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));

        sorteioService.finalizarSorteio(1L);
        verify(sorteioRepository).save(sorteio);
        verify(grupoRepository).save(grupo);
    }

    @Test
    void deveLancarBusinessExceptionAoFinalizarSorteio(){
        Grupo grupo = Grupo.builder()
                .id(1L)
                .sorteado(true)
                .build();
        Sorteio sorteio = Sorteio.builder()
                .id(1L).grupo(grupo)
                .status(StatusSorteio.FINALIZADO)
                .build();
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .grupos(List.of(grupo))
                .build();
        grupo.setDono(pessoa);
        Usuario usuario = Usuario.builder()
                .Id(1L)
                .email("teste@email.com")
                .pessoa(pessoa).build();

        mockUsuarioAutenticado(usuario);

        when(sorteioRepository.findById(1L)).thenReturn(Optional.of(sorteio));

        assertThrows(BusinessException.class, () -> sorteioService.finalizarSorteio(1L));
    }

    @Test
    void deveLancarNotFoundExceptionAoFinalizarSorteio(){
        Grupo grupo = Grupo.builder()
                .id(1L)
                .sorteado(true)
                .build();
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .grupos(List.of(grupo))
                .build();
        grupo.setDono(pessoa);
        Usuario usuario = Usuario.builder()
                .Id(1L)
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();

        mockUsuarioAutenticado(usuario);

        assertThrows(NotFoundException.class, () -> sorteioService.finalizarSorteio(1L));
    }

    @Test
    void deveRealizarSorteioComSucesso() {
        Grupo grupo = Grupo.builder()
                .id(1L)
                .sorteado(false)
                .build();

        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa 1")
                .email("pessoa1@email.com")
                .grupos(List.of(grupo))
                .build();

        Pessoa pessoa2 = Pessoa.builder()
                .id(2L)
                .nome("Pessoa 2")
                .email("pessoa2@email.com")
                .grupos(List.of(grupo))
                .build();

        grupo.setDono(pessoa);

        Sorteio sorteio = Sorteio.builder()
                .id(1L)
                .grupo(grupo)
                .status(StatusSorteio.EM_ANDAMENTO)
                .build();

        Usuario usuario = Usuario.builder()
                .Id(1L)
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();

        mockUsuarioAutenticado(usuario);

        when(sorteioRepository.findById(1L))
                .thenReturn(Optional.of(sorteio));

        when(pessoaRepository.findAllByGrupos_Id(1L))
                .thenReturn(new ArrayList<>(List.of(pessoa, pessoa2)));

        sorteioService.realizarSorteio(1L);

        ArgumentCaptor<ResultadoSorteio> captor =
                ArgumentCaptor.forClass(ResultadoSorteio.class);

        verify(resultadoSorteioRepository, times(2))
                .save(captor.capture());

        List<ResultadoSorteio> resultados = captor.getAllValues();

        assertEquals(2, resultados.size());

        for (ResultadoSorteio resultado : resultados) {
            assertNotNull(resultado.getSorteador());
            assertNotNull(resultado.getSorteado());
            assertNotEquals(
                    resultado.getSorteador().getId(),
                    resultado.getSorteado().getId()
            );
            assertEquals(sorteio, resultado.getSorteio());
        }

        verify(emailService, times(2))
                .enviarResultadoSorteio(
                        anyString(),
                        anyString(),
                        anyString()
                );
    }

    @Test
    void deveLancarNotFoundExceptionAoRealizarSorteio() {
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa")
                .build();

        Usuario usuario = Usuario.builder()
                .Id(1L)
                .pessoa(pessoa)
                .build();

        mockUsuarioAutenticado(usuario);

        when(sorteioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> sorteioService.realizarSorteio(1L)
        );

        verify(sorteioRepository).findById(1L);
    }

    @Test
    void deveLancarBusinessExceptionQuandoUsuarioNaoForDono() {
        Pessoa dono = Pessoa.builder()
                .id(2L)
                .nome("Dono")
                .build();

        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Usuario")
                .build();

        Grupo grupo = Grupo.builder()
                .id(1L)
                .dono(dono)
                .build();

        Sorteio sorteio = Sorteio.builder()
                .id(1L)
                .grupo(grupo)
                .status(StatusSorteio.EM_ANDAMENTO)
                .build();

        Usuario usuario = Usuario.builder()
                .Id(1L)
                .pessoa(pessoa)
                .build();

        mockUsuarioAutenticado(usuario);

        when(sorteioRepository.findById(1L))
                .thenReturn(Optional.of(sorteio));

        assertThrows(
                BusinessException.class,
                () -> sorteioService.realizarSorteio(1L)
        );

        verify(sorteioRepository).findById(1L);
        verifyNoInteractions(pessoaRepository);
        verifyNoInteractions(resultadoSorteioRepository);
    }

    @Test
    void deveLancarBusinessExceptionQuandoGrupoTiverMenosDeDuasPessoas() {
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa")
                .build();

        Grupo grupo = Grupo.builder()
                .id(1L)
                .dono(pessoa)
                .build();

        Sorteio sorteio = Sorteio.builder()
                .id(1L)
                .grupo(grupo)
                .status(StatusSorteio.EM_ANDAMENTO)
                .build();

        Usuario usuario = Usuario.builder()
                .Id(1L)
                .pessoa(pessoa)
                .build();

        mockUsuarioAutenticado(usuario);

        when(sorteioRepository.findById(1L))
                .thenReturn(Optional.of(sorteio));

        when(pessoaRepository.findAllByGrupos_Id(1L))
                .thenReturn(new ArrayList<>(List.of(pessoa)));

        assertThrows(
                BusinessException.class,
                () -> sorteioService.realizarSorteio(1L)
        );

        verify(pessoaRepository).findAllByGrupos_Id(1L);
        verifyNoInteractions(resultadoSorteioRepository);
    }

    @Test
    void deveLancarBusinessExceptionQuandoSorteioJaEstiverFinalizado() {
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa")
                .build();

        Pessoa pessoa2 = Pessoa.builder()
                .id(2L)
                .nome("Pessoa 2")
                .build();

        Grupo grupo = Grupo.builder()
                .id(1L)
                .dono(pessoa)
                .build();

        Sorteio sorteio = Sorteio.builder()
                .id(1L)
                .grupo(grupo)
                .status(StatusSorteio.FINALIZADO)
                .build();

        Usuario usuario = Usuario.builder()
                .Id(1L)
                .pessoa(pessoa)
                .build();

        mockUsuarioAutenticado(usuario);

        when(sorteioRepository.findById(1L))
                .thenReturn(Optional.of(sorteio));

        when(pessoaRepository.findAllByGrupos_Id(1L))
                .thenReturn(new ArrayList<>(List.of(pessoa, pessoa2)));

        assertThrows(
                BusinessException.class,
                () -> sorteioService.realizarSorteio(1L)
        );

        verify(pessoaRepository).findAllByGrupos_Id(1L);
        verifyNoInteractions(resultadoSorteioRepository);
        verifyNoInteractions(emailService);
    }
}
