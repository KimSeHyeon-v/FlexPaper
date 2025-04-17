package org.zerock.FlexPaper.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface S3Service {

    public Map<String, String> uploadFile(MultipartFile file);
    public List<String> getImages();
}
