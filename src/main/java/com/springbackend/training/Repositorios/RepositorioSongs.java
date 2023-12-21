package com.springbackend.training.Repositorios;

import com.springbackend.training.Entidades.SongsDB;
import com.springbackend.training.Repositorios.Base.RepositorioBase;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioSongs extends RepositorioBase<SongsDB, Long> {
}
