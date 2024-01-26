package com.springbackend.training.Repositorios;

import com.springbackend.training.Entidades.SongsDB;
import com.springbackend.training.Repositorios.Base.RepositorioBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioSongs extends RepositorioBase<SongsDB, Long> {
    @Query(nativeQuery = true, value =  "SELECT * FROM canciones WHERE usuario_id = :id")
    Page<SongsDB> getSongsDBByUsuarioPropietario_ID(@Param("id")Long id, Pageable pageable);
}
