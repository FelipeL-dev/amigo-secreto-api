package com.projeto.amigo.secreto.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projeto.amigo.secreto.entities.Grupo;
import com.projeto.amigo.secreto.entities.Sorteio;
import com.projeto.amigo.secreto.enums.StatusSorteio;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class SorteioDTO {
    private long id;

    @NotNull(message = "Grupo é obrigatório!")
    private Long grupoId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataSorteio;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private StatusSorteio status;

    public Sorteio mapToEntitie(Grupo grupo) {
        Sorteio sorteio = new Sorteio();
        sorteio.setGrupo(grupo);
        sorteio.setStatus(this.status);
        sorteio.setDataSorteio(this.dataSorteio);
        return sorteio;
    }
}
