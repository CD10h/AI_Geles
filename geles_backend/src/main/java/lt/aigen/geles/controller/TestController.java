package lt.aigen.geles.controller;

import lt.aigen.geles.models.Flower;
import lt.aigen.geles.repositories.FlowerRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    FlowerRepository flowerRepository;

    public TestController(FlowerRepository flowerRepository) {
        this.flowerRepository = flowerRepository;
    }

    @RequestMapping("/")
    public String index() {
        if (!flowerRepository.existsById(1L)) {
            flowerRepository.save(new Flower(1L, "Flower"));
        }
        return "Greetings!";
    }
}