package lt.aigen.geles.controller;

import lt.aigen.geles.annotations.Authorized;
import lt.aigen.geles.components.CurrentUser;
import lt.aigen.geles.models.Cart;
import lt.aigen.geles.models.User;
import lt.aigen.geles.models.dto.CartDTO;
import lt.aigen.geles.models.dto.UserAddDTO;
import lt.aigen.geles.models.dto.UserDTO;
import lt.aigen.geles.repositories.CartRepository;
import lt.aigen.geles.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    CurrentUser currentUser;
    UserRepository userRepository;
    CartRepository cartRepository;
    ModelMapper modelMapper;

    public UserController(CurrentUser currentUser, UserRepository userRepository, CartRepository cartRepository, ModelMapper modelMapper) {
        this.currentUser = currentUser;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/")
    @Transactional
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserAddDTO userAddDTO) {
        User user = convertFromDTO(userAddDTO);
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        user.setIsAdmin(false);

        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);
        var createdUser = userRepository.save(user);
        return new ResponseEntity<>(convertToDTO(createdUser), HttpStatus.OK);
    }

    @Authorized
    @GetMapping("/")
    public ResponseEntity<UserDTO> getUser() {
        User user = currentUser.get();
        return new ResponseEntity<>(convertToDTO(user), HttpStatus.OK);
    }

    private UserDTO convertToDTO(User user) { return modelMapper.map(user, UserDTO.class); }

    private User convertFromDTO(UserAddDTO userAddDTO) { return modelMapper.map(userAddDTO, User.class); }
}
