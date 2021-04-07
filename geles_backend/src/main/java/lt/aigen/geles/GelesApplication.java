package lt.aigen.geles;

import lt.controller.TestController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = TestController.class)
public class GelesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GelesApplication.class, args);
    }
}
