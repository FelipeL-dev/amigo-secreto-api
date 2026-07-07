package com.projeto.amigo.secreto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void enviarResultadoSorteio(String emailDestinatario, String nomeSorteador, String nomeSorteado) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDestinatario);
        message.setSubject("Resultado do Amigo Secreto! 🎁");
        message.setText(
                "Olá, " + nomeSorteador + "!\n\n" +
                        "O sorteio foi realizado e você tirou: " + nomeSorteado + "\n\n" +
                        "Boa sorte na escolha do presente! 🎉"
        );

        mailSender.send(message);
    }

    public void enviarCodigoVerificacao(String email, String nome, String codigo){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verificação de email - " + codigo);
        message.setText(
                "Olá, " + nome + "!\n\n" +
                        "Seu código de verificação é: " + codigo + "\n\n" +
                        "Digite esse código no site para ativar sua conta."
        );
        mailSender.send(message);
    }
}