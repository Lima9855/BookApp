package sf.lima.bookapp;

import connection.DataStaxAstraProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BookAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookAppApplication.class, args);
    }

}
