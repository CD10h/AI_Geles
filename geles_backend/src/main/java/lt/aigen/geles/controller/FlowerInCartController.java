package lt.aigen.geles.controller;

import lt.aigen.geles.models.Cart;
import lt.aigen.geles.models.Flower;
import lt.aigen.geles.models.FlowerInCart;
import lt.aigen.geles.models.dto.CartDTO;
import lt.aigen.geles.models.dto.FlowerDTO;
import lt.aigen.geles.models.dto.FlowerInCartDTO;
import lt.aigen.geles.repositories.CartRepository;
import lt.aigen.geles.repositories.FlowerInCartRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carts/flowers")
public class FlowerInCartController {
    FlowerInCartRepository flowerInCartRepository;
    ModelMapper modelMapper;

    public FlowerInCartController(FlowerInCartRepository flowerInCartRepository, ModelMapper modelMapper) {
        this.flowerInCartRepository = flowerInCartRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/")
    public List<FlowerInCartDTO> getFlowersInCart(@RequestParam Optional<Integer> id) {
        return flowerInCartRepository.
                findAllByName(id.orElse(0)).
                stream().
                map(this::convertToDTO).
                collect(Collectors.toList());
    }

    @PostMapping("/")
    public ResponseEntity<FlowerInCartDTO> postFlowerInCart(@RequestBody @Validated FlowerInCartDTO flowerInCartDTO) {
        var flowerInCart = convertFromDTO(flowerInCartDTO);
        flowerInCartRepository.save(flowerInCart);
        return ResponseEntity.ok(convertToDTO(flowerInCart));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id)
    {
        if (!flowerInCartRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        flowerInCartRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    private FlowerInCartDTO convertToDTO(FlowerInCart flowerInCart) { return modelMapper.map(flowerInCart, FlowerInCartDTO.class); }

    private FlowerInCart convertFromDTO(FlowerInCartDTO flowerInCartDTO) { return modelMapper.map(flowerInCartDTO, FlowerInCart.class); }
}
