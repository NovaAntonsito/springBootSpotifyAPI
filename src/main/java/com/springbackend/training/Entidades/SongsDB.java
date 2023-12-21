package com.springbackend.training.Entidades;


import com.springbackend.training.Entidades.Base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "canciones")
public class SongsDB extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UserDB usuarioPropietario;

    private String trackName;
    private String ArtistName;
    private String previewURL;
}
