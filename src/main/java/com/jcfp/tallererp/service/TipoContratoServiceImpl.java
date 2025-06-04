package com.jcfp.tallererp.service;

import com.jcfp.tallererp.model.TipoContrato;
import com.jcfp.tallererp.repository.TipoContratoRepository;
import com.jcfp.tallererp.util.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TipoContratoServiceImpl extends BaseServiceImpl<TipoContrato, Long, TipoContratoRepository> implements TipoContratoService{

    @Autowired
    public TipoContratoServiceImpl(TipoContratoRepository repository) {
        super(repository);
    }

    @Override
    public List<TipoContrato> findByFilterParam(String filter) {
        return List.of();
    }
}
