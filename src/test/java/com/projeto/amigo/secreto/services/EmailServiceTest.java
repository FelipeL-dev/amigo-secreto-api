package com.projeto.amigo.secreto.services;

import com.projeto.amigo.secreto.service.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;
    @Test
    void deveEnviarResultadoEmail(){
        emailService.enviarResultadoSorteio(
                "teste@email.com",
                "Teste",
                "Teste2");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage email = captor.getValue();

        Assertions.assertNotNull(email.getTo());
        assertEquals("teste@email.com", email.getTo()[0]);
        assertEquals("Resultado do Amigo Secreto! 🎁", email.getSubject());
        Assertions.assertNotNull(email.getText());
        assertTrue(email.getText().contains("Teste"));
        assertTrue(email.getText().contains("Teste2"));
    }

    @Test
    void deveEnviarCodigoVerificacao(){
        emailService.enviarCodigoVerificacao("teste@email.com", "teste", "123456");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage email = captor.getValue();

        assertNotNull(email.getTo());
        assertEquals("teste@email.com", email.getTo()[0]);
        assertEquals("Verificação de email - 123456", email.getSubject());
        assertNotNull(email.getText());
        assertTrue(email.getText().contains("teste"));
        assertTrue(email.getText().contains("123456"));
    }

    @Test
    void deveEnviarCodigoRecSenha(){
        emailService.enviarCodigoRecSenha("teste@email.com", "teste", "123456");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage email = captor.getValue();

        assertNotNull(email.getTo());
        assertEquals("teste@email.com", email.getTo()[0]);
        assertEquals("Recuperacao de senha - 123456", email.getSubject());
        assertNotNull(email.getText());
        assertTrue(email.getText().contains("teste"));
        assertTrue(email.getText().contains("123456"));
    }

}
