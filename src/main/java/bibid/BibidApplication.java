package bibid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BibidApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibidApplication.class, args);
    }

}
