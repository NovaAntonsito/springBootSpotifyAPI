package com.springbackend.training.Entidades;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.springbackend.training.Entidades.Base.BaseEntity;
import jakarta.persistence.Entity;

import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "persona")
public class UserDB extends BaseEntity {
    private String usuarioSpotify;

    @OneToMany(mappedBy = "usuarioPropietario")
    @JsonBackReference
    private List<SongsDB> cancionesPlaylist;
}