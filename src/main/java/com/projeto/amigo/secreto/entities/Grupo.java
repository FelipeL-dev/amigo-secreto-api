package com.projeto.amigo.secreto.entities;


import com.projeto.amigo.secreto.dtos.GrupoDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grupo {
    @Id
    @GeneratedValue
    long id;

    String nome;
    LocalDate dataCriacao;
    Boolean sorteado;
    @Column(unique = true)
    String tokenConvite;
    @ManyToOne
    @JoinColumn(name = "dono_id")
    private Pessoa dono;
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sorteio> sorteios;
    @ManyToMany(mappedBy = "grupos")
    private List<Pessoa> pessoas;

    public GrupoDTO mapToDto(){
        GrupoDTO dto = new GrupoDTO();
        dto.setId(this.getId());
        dto.setNome(this.getNome());
        dto.setDataCriacao(this.getDataCriacao());
        dto.setSorteado(this.getSorteado());
        return dto;
    }

    public void updateGrupo(String nome, Boolean sorteado){
        this.nome = nome;
        this.sorteado = sorteado;
    }
}
