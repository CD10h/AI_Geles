package lt.aigen.geles.controller;

import lt.aigen.geles.models.Cart;
import lt.aigen.geles.models.User;
import lt.aigen.geles.models.dto.CartDTO;
import lt.aigen.geles.models.dto.UserDTO;
import lt.aigen.geles.repositories.CartRepository;
import lt.aigen.geles.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    UserRepository userRepository;
    CartRepository cartRepository;
    ModelMapper modelMapper;

    public UserController(UserRepository userRepository, CartRepository cartRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/")
    @Transactional
    public ResponseEntity<User> createUser(@RequestBody @Valid User user)
    {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);
        var createdUser = userRepository.save(user);
        return new ResponseEntity<>(createdUser, HttpStatus.OK);
    }

    @GetMapping("/cart")
    public ResponseEntity<CartDTO> getCart(@CookieValue("user") String userCookie)
    {
        if (userRepository.findByUsername(userCookie).isPresent()) {
            var user = userRepository.findByUsername(userCookie);
            System.out.println(user.get().getCart().getId());
            return ResponseEntity.ok(convertToDTO(user.get().getCart()));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private UserDTO convertToDTO(User user) { return modelMapper.map(user, UserDTO.class); }

    private User convertFromDTO(UserDTO userDTO) { return modelMapper.map(userDTO, User.class); }

    private CartDTO convertToDTO(Cart cart) { return modelMapper.map(cart, CartDTO.class); }

    private Cart convertFromDTO(CartDTO cartDTO) { return modelMapper.map(cartDTO, Cart.class); }
}
