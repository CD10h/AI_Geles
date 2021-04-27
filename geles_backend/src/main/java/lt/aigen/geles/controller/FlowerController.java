package lt.aigen.geles.controller;

import lt.aigen.geles.models.Flower;
import lt.aigen.geles.repositories.FlowerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/flowers")
public class FlowerController {
    FlowerRepository flowerRepository;

    public FlowerController(FlowerRepository flowerRepository) {
        this.flowerRepository = flowerRepository;
    }

    @GetMapping("/") // /flowers/?q=gele
    public List<Flower> getFlowers(@RequestParam Optional<String> q) {
        return flowerRepository.findAllByName(q.orElse(""));
    }

    @GetMapping("/{id}") // /flowers/10
    public ResponseEntity<Flower> getFlower(@PathVariable Long id) {
        return flowerRepository.findById(id).map(ResponseEntity::ok).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/")
    public ResponseEntity<Flower> postFlower(@Valid @RequestBody Flower newFlower) {

        var createdFlower = flowerRepository.save(newFlower);
        return new ResponseEntity<>(createdFlower, HttpStatus.CREATED);
    }
}
