package vn.hcmute.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final Path uploadRoot;

    public WebConfig(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadRoot);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not create upload directory", ex);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Handle uploads directory
        String location = this.uploadRoot.toUri().toString();
        registry
            .addResourceHandler("/uploads/**")
            .addResourceLocations(location);
        
        // Handle static resources
        registry
            .addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/");
        
        // Handle favicon
        registry
            .addResourceHandler("/favicon.ico")
            .addResourceLocations("classpath:/static/images/");
    }
}