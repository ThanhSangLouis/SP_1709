package vn.hcmute.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeCategoryImage(MultipartFile file) throws IOException;

    void delete(String webPath) throws IOException;
}