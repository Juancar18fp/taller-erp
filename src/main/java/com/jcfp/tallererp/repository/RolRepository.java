package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.entity.Rol;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends BaseRepository<Rol, Long> {
    @Query("""
    select e from roles e
""")
    Page<Rol> findByFilter(@Param("filter") String filter, Pageable pageable);

    Optional<Rol> findByNombre(String nombre);
    @Query("SELECT r FROM roles r ORDER BY r.nombre")
    List<Rol> findAllRoles();

    boolean existsByNombre(String nombre);

}
