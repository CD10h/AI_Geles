package lt.aigen.geles.controller;

import lt.aigen.geles.models.Flower;
import lt.aigen.geles.repositories.FlowerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PutMapping("/update/{id}")
    ResponseEntity<Flower> updateFlower(@RequestBody Flower newFlower, @PathVariable Long id) {
        if (!id.equals(newFlower.getId())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (flowerRepository.findById(id).equals(Optional.empty())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(flowerRepository.save(newFlower), HttpStatus.OK);
    }
}