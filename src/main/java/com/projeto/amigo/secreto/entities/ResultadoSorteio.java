package com.projeto.amigo.secreto.entities;

import com.projeto.amigo.secreto.dtos.ResultadoSorteioDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoSorteio {
    @Id
    @GeneratedValue
    long id;

    @ManyToOne
    @JoinColumn(name = "sorteio_id")
    Sorteio sorteio;

    @ManyToOne
    @JoinColumn(name = "sorteador_id")
    Pessoa sorteador;

    @ManyToOne
    @JoinColumn(name = "sorteado_id")
    Pessoa sorteado;

    public ResultadoSorteioDTO mapToDto(){
        ResultadoSorteioDTO dto = new ResultadoSorteioDTO();
        dto.setId(this.getId());
        if (this.getSorteio() != null){
            dto.setSorteio_id(this.getSorteio().getId());
        }
        if (this.getSorteador() != null){
            dto.setSorteador_id(this.getSorteador().getId());
        }
        if(this.getSorteado() != null){
            dto.setSorteado_id(this.getSorteado().getId());
        }
        return dto;
    }

}
