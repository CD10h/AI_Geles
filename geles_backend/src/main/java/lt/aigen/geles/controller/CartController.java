package lt.aigen.geles.controller;

import lt.aigen.geles.repositories.CartRepository;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {
    CartRepository cartRepository;
    ModelMapper modelMapper;

    public CartController(CartRepository cartRepository, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
    }
}