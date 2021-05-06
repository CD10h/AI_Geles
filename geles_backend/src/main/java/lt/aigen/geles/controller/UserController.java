package lt.aigen.geles.controller;

import lt.aigen.geles.models.User;
import lt.aigen.geles.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/")
    public ResponseEntity<User> createUser(@RequestBody @Valid User user)
    {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        var createdUser = userRepository.save(user);
        return new ResponseEntity<>(createdUser, HttpStatus.OK);
    }
}
