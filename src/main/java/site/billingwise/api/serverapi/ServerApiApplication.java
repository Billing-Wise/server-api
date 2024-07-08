package site.billingwise.api.serverapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@PropertySource("classpath:secure.properties")
public class ServerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApiApplication.class, args);
    }

}
