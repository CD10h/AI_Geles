package lt.aigen.geles.controller;

import lt.aigen.geles.models.Flower;
import lt.aigen.geles.repositories.FlowerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController("/flowers")
public class FlowerController {
    FlowerRepository flowerRepository;

    public FlowerController(FlowerRepository flowerRepository) {
        this.flowerRepository = flowerRepository;
    }

    @GetMapping("/")
    public String index() {
        return "Greetings!";
    }

    @GetMapping("/search") // /flowers/?q=gele
    @ResponseBody
    public List<Flower> search(@RequestParam String q) {
        return flowerRepository.findAllByName(q);
    }

    @GetMapping("/{id}") // /flowers/10
    @ResponseBody
    public ResponseEntity<Flower> getFlower(@PathVariable Long id) {
        return flowerRepository.findById(id).map(ResponseEntity::ok).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
