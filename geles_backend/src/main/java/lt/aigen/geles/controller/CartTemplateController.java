package lt.aigen.geles.controller;

import lt.aigen.geles.models.*;
import lt.aigen.geles.models.dto.CartDTO;
import lt.aigen.geles.models.dto.CartTemplateDTO;
import lt.aigen.geles.models.dto.FlowerDTO;
import lt.aigen.geles.models.dto.FlowerInCartDTO;
import lt.aigen.geles.repositories.CartRepository;
import lt.aigen.geles.repositories.CartTemplateRepository;
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
@RequestMapping("/templates")
public class CartTemplateController {
    CartTemplateRepository cartTemplateRepository;
    UserRepository userRepository;
    FlowerInCartRepository flowerInCartRepository;
    ModelMapper modelMapper;

    public CartTemplateController(CartTemplateRepository cartTemplateRepository, UserRepository userRepository, FlowerInCartRepository flowerInCartRepository, ModelMapper modelMapper) {
        this.cartTemplateRepository = cartTemplateRepository;
        this.userRepository = userRepository;
        this.flowerInCartRepository = flowerInCartRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/")
    public List<CartTemplateDTO> getCartTemplate(@CookieValue("user") String userCookie) {

        var user = userRepository.findByUsername(userCookie);

        return cartTemplateRepository.
                findAllByUser(user.get().getId()).
                stream().
                map(this::convertToDTO).
                collect(Collectors.toList());

    }

    @Transactional
    @PostMapping("/")
    public ResponseEntity<CartTemplateDTO> postCartTemplate(@RequestBody @Validated CartTemplateDTO cartTemplateDTO, @CookieValue("user") String userCookie){

        List<FlowerInCartDTO> flowerInCartDTOs = cartTemplateDTO.getFlowersInCart();
        List<FlowerInCart> flowersInCart = new ArrayList<>();
        Optional<User> user = userRepository.findByUsername(userCookie);

        CartTemplate cartTemplate = convertFromDTO(cartTemplateDTO);
        cartTemplate.setUser(user.get());
        CartTemplate savedTemplate = cartTemplateRepository.save(cartTemplate);

        for(var f: flowerInCartDTOs){
            FlowerInCart flowerInCart = convertFromDTO(f);
            flowerInCart.setCart(null);
            flowerInCart.setCartTemplate(savedTemplate);
            flowersInCart.add(flowerInCartRepository.save(flowerInCart));
        }

        cartTemplate.setFlowersInCart(flowersInCart);
        cartTemplateRepository.save(cartTemplate);

        return new ResponseEntity<>(convertToDTO(cartTemplate), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id)
    {
        if (!cartTemplateRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<CartTemplate> cartTemplate = cartTemplateRepository.findById(id);
        List<FlowerInCart> flowersInTemplate = cartTemplate.get().getFlowersInCart();

        for(var f: flowersInTemplate){
            flowerInCartRepository.delete(f);
        }

        cartTemplateRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private CartTemplateDTO convertToDTO(CartTemplate cartTemplate) { return modelMapper.map(cartTemplate, CartTemplateDTO.class); }

    private CartTemplate convertFromDTO(CartTemplateDTO cartTemplateDTO) { return modelMapper.map(cartTemplateDTO, CartTemplate.class); }

    private FlowerInCart convertFromDTO(FlowerInCartDTO flowerInCartDTO) { return modelMapper.map(flowerInCartDTO, FlowerInCart.class); }
}
