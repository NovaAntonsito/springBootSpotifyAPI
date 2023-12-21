package com.springbackend.training.Servicios;

import com.springbackend.training.Entidades.SongsDB;
import com.springbackend.training.Repositorios.Base.RepositorioBase;
import com.springbackend.training.Servicios.Base.ServicioBase;
import com.springbackend.training.Servicios.Interfaces.ISongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SongService extends ServicioBase<SongsDB, Long> implements ISongService {



    public SongService(RepositorioBase<SongsDB, Long> baseRepository) {
        super(baseRepository);
    }


}
