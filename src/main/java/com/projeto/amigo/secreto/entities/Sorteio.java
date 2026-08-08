package com.projeto.amigo.secreto.entities;

import com.projeto.amigo.secreto.dtos.SorteioDTO;
import com.projeto.amigo.secreto.enums.StatusSorteio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sorteio {
    @Id @GeneratedValue
    long id;

    @ManyToOne
    private Grupo grupo;

    LocalDateTime dataSorteio;

    @OneToMany(mappedBy = "sorteio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResultadoSorteio> resultados;

    @Enumerated(EnumType.STRING)
    private StatusSorteio status;

    public SorteioDTO mapToDto() {
        if (this.getGrupo() != null) {
            return SorteioDTO.builder()
                    .id(this.getId())
                    .status(this.getStatus())
                    .dataSorteio(this.getDataSorteio())
                    .grupoId(this.getGrupo().getId())
                    .build();
        }

        return SorteioDTO.builder()
                .id(this.getId())
                .status(this.getStatus())
                .dataSorteio(this.getDataSorteio())
                .build();
    }
}
