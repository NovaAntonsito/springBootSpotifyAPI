package com.springbackend.training.Servicios.Base;

import com.springbackend.training.Entidades.Base.BaseEntity;
import com.springbackend.training.Repositorios.Base.RepositorioBase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class ServicioBase<E extends BaseEntity, ID extends Serializable> implements IServicioBase<E,ID> {

    protected RepositorioBase<E,ID> BaseRepository;

    public ServicioBase(RepositorioBase<E, ID> baseRepository) {
        this.BaseRepository = baseRepository;
    }


    @Override
    @Transactional
    public List<E> findAll() throws Exception {
        try {
            if(!BaseRepository.findAll().isEmpty()){
                return BaseRepository.findAll();
            }
            throw new Exception("La base de datos esta vacia");
        }catch (Exception e) {
            log.info(e.getMessage());
            throw new Exception(e.getMessage());
        }

    }

    @Override
    @Transactional
    public Page<E> findAllPaged(Pageable pageable) throws Exception {
        try {
            return BaseRepository.findAll(pageable);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    @Override
    @Transactional
    public E findById(ID id) throws Exception {
        try {
            Optional<E> entityOptional = BaseRepository.findById(id);
            return entityOptional.get();

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public E save(E entity) throws Exception {
        try {
            entity = BaseRepository.save(entity);
            return entity;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public E update(ID id, E entity) throws Exception {
        try {
            Optional<E> entityOptional = BaseRepository.findById(id);
            E persona = entityOptional.get();
            persona = BaseRepository.save(entity);
            return persona;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean delete(ID id) throws Exception {
        try {
            if (BaseRepository.existsById(id)){
                BaseRepository.deleteById(id);
                return true;
            }else{
                throw new Exception();
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
