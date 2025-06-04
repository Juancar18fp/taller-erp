package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.JornadaLaboral;
import com.jcfp.tallererp.repository.JornadaLaboralRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class JornadaLaboralServiceImpl extends BaseServiceImpl<JornadaLaboral, Long ,JornadaLaboralRepository> implements JornadaLaboralService {

    @Autowired
    public JornadaLaboralServiceImpl(JornadaLaboralRepository repository) {
        super(repository);
    }

    @Override
    public List<JornadaLaboral> findByFilterParam(String filter) {
        return List.of();
    }
}
