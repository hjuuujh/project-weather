package zerobase.projectweather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication

public class ProjectWeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectWeatherApplication.class, args);
    }

}
