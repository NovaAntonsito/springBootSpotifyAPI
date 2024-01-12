package com.springbackend.training.Repositorios;


import com.springbackend.training.Entidades.UserDB;
import com.springbackend.training.Repositorios.Base.RepositorioBase;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioUser extends RepositorioBase<UserDB, Long> {

    boolean existsByUsuarioSpotify(String name);
}
