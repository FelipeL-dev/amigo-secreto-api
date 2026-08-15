package com.projeto.amigo.secreto.dtos;


import com.projeto.amigo.secreto.entities.Pessoa;
import com.projeto.amigo.secreto.entities.ResultadoSorteio;
import com.projeto.amigo.secreto.entities.Sorteio;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultadoSorteioDTO {
    private long id;

    @NotNull(message = "Sorteio é obrigatório!")
    private long sorteio_id;
    @NotNull(message = "Sorteador é obrigatório!")
    private long sorteador_id;
    @NotNull(message = "Sorteado é obrigatório!")
    private long sorteado_id;

    public ResultadoSorteio mapToEntite(Sorteio sorteio, Pessoa sorteador, Pessoa sorteado){
        ResultadoSorteio resultadoSorteio = new ResultadoSorteio();
        resultadoSorteio.setSorteio(sorteio);
        resultadoSorteio.setSorteado(sorteado);
        resultadoSorteio.setSorteador(sorteador);
        return resultadoSorteio;
    }

}
