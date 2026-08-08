package com.projeto.amigo.secreto.entities;


import com.projeto.amigo.secreto.dtos.GrupoDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
        return GrupoDTO.builder().id(this.getId()).nome(this.getNome()).dataCriacao(this.getDataCriacao()).sorteado(this.getSorteado()).build();
    }

    public void updateGrupo(String nome){
        this.nome = nome;
    }
}
