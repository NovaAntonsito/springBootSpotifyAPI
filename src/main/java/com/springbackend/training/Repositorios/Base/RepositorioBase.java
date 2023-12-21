package com.springbackend.training.Repositorios.Base;

import com.springbackend.training.Entidades.Base.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface RepositorioBase <E extends BaseEntity, ID extends Serializable> extends JpaRepository<E,ID> {

}
