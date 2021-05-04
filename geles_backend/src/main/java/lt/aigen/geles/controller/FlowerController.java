package lt.aigen.geles.controller;

import lt.aigen.geles.models.dto.FlowerDTO;
import lt.aigen.geles.models.Flower;
import lt.aigen.geles.repositories.FlowerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/flowers")
public class FlowerController {
    FlowerRepository flowerRepository;

    ModelMapper modelMapper;

    public FlowerController(FlowerRepository flowerRepository, ModelMapper modelMapper) {
        this.flowerRepository = flowerRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/") // /flowers/?q=gele
    public List<FlowerDTO> getFlowers(@RequestParam Optional<String> q) {
        return flowerRepository.
                findAllByName(q.orElse("")).
                stream().
                map(this::convertToDTO).
                collect(Collectors.toList());
    }

    @GetMapping("/{id}") // /flowers/10
    public ResponseEntity<FlowerDTO> getFlower(@PathVariable Long id) {
        var flower = flowerRepository.findById(id);
        if (flower.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            var s = flower.get().getSeller();
            return ResponseEntity.ok(convertToDTO(flower.get()));
        }
    }

    @PostMapping("/")
    public ResponseEntity<FlowerDTO> postFlower(@RequestBody @Valid FlowerDTO flowerDTO) {
        var flower = convertFromDTO(flowerDTO);
        flowerRepository.save(flower);
        return ResponseEntity.ok(convertToDTO(flower));
    }

    private FlowerDTO convertToDTO(Flower flower) {
        return modelMapper.map(flower, FlowerDTO.class);
    }

    private Flower convertFromDTO(FlowerDTO flowerDTO) {
        return modelMapper.map(flowerDTO, Flower.class);
    }

}
