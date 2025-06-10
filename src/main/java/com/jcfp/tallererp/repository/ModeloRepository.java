package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.entity.Marca;
import com.jcfp.tallererp.entity.Modelo;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModeloRepository extends BaseRepository<Modelo, Long> {
    @Query("""
    select e from modelos e
    where lower(e.nombre) like lower(concat('%', :filter, '%'))
    or lower(e.marca.nombre) like lower(concat('%', :filter, '%'))
""")
    Page<Modelo> findByFilter(@Param("filter") String filter, Pageable pageable);
    List<Modelo> findByMarca_IdAndNombreContainingIgnoreCase(Long marcaId, String modeloNombre);
    Optional<Modelo> findByNombre(String nombre);
    Optional<Modelo> findByNombreAndMarca(String nombre, Marca marca);
}
