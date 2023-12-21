package com.springbackend.training.Controladores.Base;

import com.springbackend.training.Entidades.Base.BaseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Pageable;
import java.io.Serializable;

public interface IControladorBase<E extends BaseEntity, ID extends Serializable> {
    public ResponseEntity<?> getAll();
    public ResponseEntity<?> getAllPaged(Pageable pageable);
    public ResponseEntity<?> getOne(@PathVariable ID id);
    public ResponseEntity<?> save(@RequestBody E entity);
    public ResponseEntity<?> update(@PathVariable ID id,@RequestBody E entity);
    public ResponseEntity<?> delete(@PathVariable ID id);
}
