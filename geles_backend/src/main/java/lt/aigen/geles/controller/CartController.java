package lt.aigen.geles.controller;

import lt.aigen.geles.annotations.Authorized;
import lt.aigen.geles.components.CurrentUser;
import lt.aigen.geles.models.Cart;
import lt.aigen.geles.models.Flower;
import lt.aigen.geles.models.FlowerInCart;
import lt.aigen.geles.models.User;
import lt.aigen.geles.models.dto.CartDTO;
import lt.aigen.geles.models.dto.FlowerDTO;
import lt.aigen.geles.models.dto.FlowerInCartDTO;
import lt.aigen.geles.repositories.CartRepository;
import lt.aigen.geles.repositories.FlowerInCartRepository;
import lt.aigen.geles.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carts")
public class CartController {
    CartRepository cartRepository;
    CurrentUser currentUser;
    FlowerInCartRepository flowerInCartRepository;
    ModelMapper modelMapper;

    public CartController(CartRepository cartRepository, CurrentUser currentUser, FlowerInCartRepository flowerInCartRepository, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.currentUser = currentUser;
        this.flowerInCartRepository = flowerInCartRepository;
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
    @Transactional
    ResponseEntity<CartDTO> updateCart(@RequestBody @Validated CartDTO cartDTO, @PathVariable Long id) {
        Optional<Cart> oldCart = cartRepository.findById(id);
        if(oldCart.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Cart newCart = oldCart.get();

        for(var f: newCart.getFlowersInCart()){
            flowerInCartRepository.delete(f);
        }

        List<FlowerInCart> flowersInCart = new ArrayList<>();
        for (var f : cartDTO.getFlowersInCart()) {
            FlowerInCart flowerInCart = convertFromDTO(f);
            flowerInCart.setCart(newCart);
            flowerInCart = flowerInCartRepository.save(flowerInCart);
            flowersInCart.add(flowerInCart);
            System.out.println(flowerInCart);
        }

        newCart.setFlowersInCart(flowersInCart);
        return new ResponseEntity<>(convertToDTO(newCart), HttpStatus.OK);
    }

    private CartDTO convertToDTO(Cart cart) { return modelMapper.map(cart, CartDTO.class); }

    private Cart convertFromDTO(CartDTO cartDTO) { return modelMapper.map(cartDTO, Cart.class); }

    private FlowerInCart convertFromDTO(FlowerInCartDTO flowerInCartDTO) { return modelMapper.map(flowerInCartDTO, FlowerInCart.class); }
}