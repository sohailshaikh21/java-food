package foodie_backend.foodie_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class FoodieBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodieBackendApplication.class, args);
	}

}
