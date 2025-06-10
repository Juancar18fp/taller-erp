package com.jcfp.tallererp.service;

import com.jcfp.tallererp.dto.ImportResultDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CsvService {
    Resource exportToCSV(String entity) throws Exception;
    Resource generateTemplate(String entity) throws Exception;
    ImportResultDto importFromCSV(String entity, List<Map<String, Object>> data);
    ImportResultDto importFromFile(String entity, MultipartFile file) throws Exception;
}
