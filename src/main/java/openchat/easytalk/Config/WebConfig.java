package openchat.easytalk.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")// Specify the allowed origin(s)
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Specify allowed request methods
                .allowedHeaders("Content-Type", "Authorization") // Specify allowed headers
                .exposedHeaders("Authorization")
                .allowCredentials(true);

    }
}
