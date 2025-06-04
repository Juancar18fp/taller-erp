package com.jcfp.tallererp.repository;

import com.jcfp.tallererp.model.Rol;
import com.jcfp.tallererp.util.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends BaseRepository<Rol, Long> {
    @Query("""
    select e from roles e
""")
    Page<Rol> findByFilter(@Param("filter") String filter, Pageable pageable);

}
