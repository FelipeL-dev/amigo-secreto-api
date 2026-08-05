package com.projeto.amigo.secreto.services;

import com.projeto.amigo.secreto.dtos.GrupoDTO;
import com.projeto.amigo.secreto.entities.Grupo;
import com.projeto.amigo.secreto.entities.Pessoa;
import com.projeto.amigo.secreto.entities.Usuario;
import com.projeto.amigo.secreto.exceptions.BusinessException;
import com.projeto.amigo.secreto.exceptions.NotFoundException;
import com.projeto.amigo.secreto.exceptions.UnauthorizedException;
import com.projeto.amigo.secreto.repositories.GrupoRepository;
import com.projeto.amigo.secreto.repositories.PessoaRepository;
import com.projeto.amigo.secreto.service.GrupoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GrupoServiceTest {
    @Mock
    GrupoRepository grupoRepository;

    @Mock
    PessoaRepository pessoaRepository;

    @InjectMocks
    GrupoService grupoService;

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    private void mockUsuarioAutenticado(Usuario usuario) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getPrincipal()).thenReturn(usuario);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void deveCriarGrupo(){
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();

        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();

        mockUsuarioAutenticado(usuario);

        GrupoDTO dto = new GrupoDTO();
        dto.setNome("Grupo Teste");

        when(pessoaRepository.findById(1L))
                .thenReturn(Optional.of(pessoa));

        // Act
        GrupoDTO response = grupoService.create(dto);

        // Assert
        verify(grupoRepository).save(any(Grupo.class));
        verify(pessoaRepository).save(pessoa);

        assertEquals("Grupo Teste", response.getNome());
        assertEquals(1, pessoa.getGrupos().size());
    }

    @Test
    void deveLancarExcecaoNotFoundAoCriarGrupo(){
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();

       mockUsuarioAutenticado(usuario);
        GrupoDTO dto = new GrupoDTO();
        when(pessoaRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class , () -> grupoService.create(dto));
    }

    @Test
    void deveRetornarTodosOsGruposDoSistema(){
        Grupo grupo1 = Grupo.builder().id(1).nome("Grupo 1").build();
        Grupo grupo2 = Grupo.builder().id(2).nome("Grupo 2").build();

      when(grupoRepository.findAll()).thenReturn(List.of(grupo1, grupo2));

      List<GrupoDTO> grupos = grupoService.findAll();

      assertEquals(2, grupos.size());
      assertEquals("Grupo 1", grupos.get(0).getNome());
      assertEquals("Grupo 2", grupos.get(1).getNome());
      verify(grupoRepository).findAll();
    }

    @Test
    void deveRetornarOsGruposDoUsuario() {

        Grupo grupo1 = Grupo.builder().id(1L).nome("Grupo 1").build();
        Grupo grupo2 = Grupo.builder().id(2L).nome("Grupo 2").build();

        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Teste")
                .grupos(new ArrayList<>(List.of(grupo1, grupo2)))
                .build();

        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();

       mockUsuarioAutenticado(usuario);

        when(pessoaRepository.findById(1L))
                .thenReturn(Optional.of(pessoa));

        List<GrupoDTO> response = grupoService.findMeusGrupos();

        assertEquals(2, response.size());
        assertEquals("Grupo 1", response.get(0).getNome());
        assertEquals("Grupo 2", response.get(1).getNome());

        verify(pessoaRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoNotFoundAoBuscarOsGruposDoUsuario(){
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();

        mockUsuarioAutenticado(usuario);
        when(pessoaRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class , () -> grupoService.findMeusGrupos());
    }

    @Test
    void deveEncontrarGrupoPorId(){
        Grupo grupo = Grupo.builder().id(1L).nome("Grupo Teste").build();

        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));

        GrupoDTO dto = grupoService.findById(1L);
        assertEquals("Grupo Teste", dto.getNome());
    }

    @Test
    void deveLancarExcecaoNotFoundAoBuscarGrupoPorId(){
        when(grupoRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class , () -> grupoService.findById(1L));
    }

    @Test
    void deveDeletarGrupo(){

        Pessoa pessoa = Pessoa.builder().id(1L).nome("teste").grupos(new ArrayList<>()).build();
        Grupo grupo = Grupo.builder().id(1L).nome("teste").dono(pessoa).build();
        pessoa.getGrupos().add(grupo);
        Usuario usuario = Usuario.builder().pessoa(pessoa).Id(1L).email("teste@email.com").build();

        mockUsuarioAutenticado(usuario);

        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));

        grupoService.delete(1L);

        verify(grupoRepository).delete(grupo);
    }

    @Test
    void deveLancarExcecaoNotFoundAoDeletarGrupo(){
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();

        mockUsuarioAutenticado(usuario);
        when(grupoRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class , () -> grupoService.delete(1L));
    }

    @Test
    void deveLancarUnauthorizedAccessExceptionAoDeletarGrupo(){
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();
        Pessoa dono = Pessoa.builder()
                .id(2L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();
        Grupo grupo = Grupo.builder()
                .id(1L).dono(dono).
                build();

        mockUsuarioAutenticado(usuario);

        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));

        assertThrows(UnauthorizedException.class, ()-> grupoService.delete(1L));

    }

    @Test
    void deveAtualizarOGrupo(){
        Pessoa pessoa = Pessoa.builder().id(1L).nome("teste").build();
        Usuario usuario = Usuario.builder().email("teste@email.com").Id(1L).pessoa(pessoa).build();
        Grupo grupo = Grupo.builder().id(1L).nome("teste").dono(pessoa).build();

        mockUsuarioAutenticado(usuario);

        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));
        GrupoDTO grupoUpdated = grupoService.update(1L, "teste2");

        verify(grupoRepository).save(grupo);
        assertEquals("teste2", grupoUpdated.getNome());
    }

    @Test
    void deveLancarExcecaoNotFoundAoAtualizarGrupo(){
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();
        Grupo grupo = Grupo.builder().id(1L).nome("teste").dono(pessoa).build();

        mockUsuarioAutenticado(usuario);
        when(grupoRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class , () -> grupoService.update(1L,grupo.getNome()));
    }

    @Test
    void deveLancarUnauthorizedAccessExceptionAoAtualizarGrupo(){
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();
        Pessoa dono = Pessoa.builder()
                .id(2L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();
        Grupo grupo = Grupo.builder()
                .id(1L).dono(dono).
                build();

        mockUsuarioAutenticado(usuario);

        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));

        assertThrows(UnauthorizedException.class, ()-> grupoService.update(1L, "teste"));
    }

    @Test
    void deveBuscarGruposSorteados(){
        Grupo grupo = Grupo.builder().id(1L).nome("teste").sorteado(true).build();
        List<Grupo> grupos = new ArrayList<>();
        grupos.add(grupo);
        
        when(grupoRepository.findBySorteado(true)).thenReturn(grupos);

        List<GrupoDTO> gruposSorteados = grupoService.findGruposSorteados();

        assertEquals("teste", gruposSorteados.getFirst().getNome());
    }

    @Test
    void deveGerarConviteGrupo(){
        Pessoa pessoa = Pessoa.builder().nome("teste").id(1L).build();
        Grupo grupo = Grupo.builder().id(1L).nome("grupo teste").dono(pessoa).build();
        Usuario usuario = Usuario.builder().Id(1L).email("teste@email.com").pessoa(pessoa).build();

        mockUsuarioAutenticado(usuario);

        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));

        String conviteToken = grupoService.gerarConviteToken(1L);

        assertNotNull(conviteToken);
        assertEquals(conviteToken, grupo.getTokenConvite());
        verify(grupoRepository).save(grupo);
    }

    @Test
    void deveLancarExcecaoNotFoundAoGerarConvite(){
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();

        mockUsuarioAutenticado(usuario);
        when(grupoRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class , () -> grupoService.gerarConviteToken(1L));
    }

    @Test
    void deveLancarUnauthorizedAccessExceptionAoGerarConvite(){
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();
        Pessoa dono = Pessoa.builder()
                .id(2L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();
        Grupo grupo = Grupo.builder()
                .id(1L).dono(dono).
                build();

        mockUsuarioAutenticado(usuario);

        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));

        assertThrows(UnauthorizedException.class, ()-> grupoService.gerarConviteToken(1L));

    }

    @Test
    void deveEntrarNoGrupoComSucesso(){
        Pessoa dono = Pessoa.builder().id(2L).nome("dono").grupos(new ArrayList<>()).build();
        Pessoa pessoa = Pessoa.builder().id(1L).nome("teste").grupos(new ArrayList<>()).build();
        Grupo grupo = Grupo.builder().id(1L).dono(dono).nome("grupo teste").sorteado(false).tokenConvite("123456").build();
        dono.getGrupos().add(grupo);
        Usuario usuario = Usuario.builder().pessoa(pessoa).email("teste@email.com").build();

        mockUsuarioAutenticado(usuario);

        when(grupoRepository.findByTokenConvite("123456")).thenReturn(Optional.of(grupo));
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));


        grupoService.entrarNoGrupo(grupo.getTokenConvite());
        assertTrue(pessoa.getGrupos().contains(grupo));
        verify(pessoaRepository).save(pessoa);
        verify(grupoRepository).findByTokenConvite("123456");
        verify(pessoaRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoNotFoundAoEntrarNoGrupo(){
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();
        Grupo grupo = Grupo.builder().id(1L).nome("teste").tokenConvite("123456").dono(pessoa).build();

        mockUsuarioAutenticado(usuario);

        when(grupoRepository.findByTokenConvite(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class , () -> grupoService.entrarNoGrupo(grupo.getTokenConvite()));
    }

    @Test
    void deveLancarExcecaoNotFoundPessoaAoEntrarNoGrupo(){
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();
        Grupo grupo = Grupo.builder().id(1L).nome("teste").tokenConvite("123456").dono(pessoa).build();

        mockUsuarioAutenticado(usuario);
        when(grupoRepository.findByTokenConvite(grupo.getTokenConvite())).thenReturn(Optional.of(grupo));
        when(pessoaRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class , () -> grupoService.entrarNoGrupo(grupo.getTokenConvite()));
    }

    @Test
    void deveLancarBusinessExceptionAoEntrarNoGrupo(){
        Pessoa pessoa = Pessoa.builder()
                .id(1L)
                .nome("Pessoa Teste")
                .grupos(new ArrayList<>())
                .build();
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .pessoa(pessoa)
                .build();
        Grupo grupo = Grupo.builder()
                .id(1L)
                .dono(pessoa)
                .tokenConvite("123456")
                .build();
        pessoa.getGrupos().add(grupo);

        mockUsuarioAutenticado(usuario);

        when(grupoRepository.findByTokenConvite("123456")).thenReturn(Optional.of(grupo));
        when(pessoaRepository.findById(pessoa.getId())).thenReturn(Optional.of(pessoa));

        assertThrows(BusinessException.class, ()-> grupoService.entrarNoGrupo("123456"));

    }

}
