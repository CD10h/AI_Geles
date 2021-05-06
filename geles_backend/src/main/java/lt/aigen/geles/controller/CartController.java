package lt.aigen.geles.controller;

import lt.aigen.geles.models.Cart;
import lt.aigen.geles.models.Flower;
import lt.aigen.geles.models.dto.CartDTO;
import lt.aigen.geles.models.dto.FlowerDTO;
import lt.aigen.geles.repositories.CartRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carts")
public class CartController {
    CartRepository cartRepository;
    ModelMapper modelMapper;

    public CartController(CartRepository cartRepository, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{id}") // /carts/10
    public ResponseEntity<CartDTO> getCart(@PathVariable Long id) {
        var cart = cartRepository.findById(id);
        if (cart.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(convertToDTO(cart.get()));
        }
    }

    @PutMapping("/{id}")
    ResponseEntity<CartDTO> updateCart(@RequestBody @Validated CartDTO cartDTO, @PathVariable Long id) {
        if (cartRepository.findById(id).isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var newCart = convertFromDTO(cartDTO);
        newCart.setId(id);
        return new ResponseEntity<>(convertToDTO(cartRepository.save(newCart)), HttpStatus.OK);
    }

    private CartDTO convertToDTO(Cart cart) { return modelMapper.map(cart, CartDTO.class); }

    private Cart convertFromDTO(CartDTO cartDTO) { return modelMapper.map(cartDTO, Cart.class); }
}