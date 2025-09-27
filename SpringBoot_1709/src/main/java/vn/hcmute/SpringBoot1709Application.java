package vn.hcmute;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import vn.hcmute.config.StorageProperties;
import vn.hcmute.service.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class) // thêm cấu hình storage
@org.springframework.data.jpa.repository.config.EnableJpaRepositories(basePackages = "vn.hcmute.repository")
@org.springframework.boot.autoconfigure.domain.EntityScan(basePackages = "vn.hcmute.entity")
public class SpringBoot1709Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBoot1709Application.class, args);
	}

	// thêm cấu hình storage
	@org.springframework.context.annotation.Bean
	CommandLineRunner init(StorageService storageService) {
		return (args -> {
			storageService.init();
		});
	}
}
