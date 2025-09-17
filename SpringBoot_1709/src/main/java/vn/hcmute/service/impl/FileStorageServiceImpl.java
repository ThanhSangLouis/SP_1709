package vn.hcmute.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import vn.hcmute.service.FileStorageService;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path rootLocation;
    private final Path categoryLocation;

    public FileStorageServiceImpl(
            @Value("${app.upload.dir:uploads}") String uploadDir,
            @Value("${app.upload.categories-dir:categories}") String categoryDir) throws IOException {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.categoryLocation = this.rootLocation.resolve(categoryDir).normalize();
        Files.createDirectories(this.categoryLocation);
    }

    @Override
    public String storeCategoryImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Uploaded file is empty");
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image")) {
            throw new IOException("Only image files are supported");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int extIndex = originalFilename.lastIndexOf('.');
        if (extIndex >= 0) {
            extension = originalFilename.substring(extIndex);
        }

        String filename = UUID.randomUUID().toString() + extension;
        Path destinationFile = this.categoryLocation.resolve(filename).normalize();

        if (!destinationFile.startsWith(this.rootLocation)) {
            throw new IOException("Cannot store file outside designated directory");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }

        String relativePath = this.rootLocation.relativize(destinationFile).toString().replace("\\", "/");
        return "/uploads/" + relativePath;
    }

    @Override
    public void delete(String webPath) throws IOException {
        if (webPath == null || webPath.isBlank()) {
            return;
        }

        String prefix = "/uploads/";
        if (!webPath.startsWith(prefix)) {
            return;
        }

        Path filePath = this.rootLocation.resolve(webPath.substring(prefix.length())).normalize();
        if (!filePath.startsWith(this.rootLocation) || !Files.exists(filePath)) {
            return;
        }

        Files.delete(filePath);
    }
}