package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.model.Marca;
import com.jcfp.tallererp.model.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long> {

    List<Modelo> findModeloByMarca(Marca marca);
}
