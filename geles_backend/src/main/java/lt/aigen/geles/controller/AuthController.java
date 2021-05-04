package lt.aigen.geles.controller;

import lt.aigen.geles.models.Login;
import lt.aigen.geles.models.User;
import lt.aigen.geles.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid Login login, HttpServletResponse response)
    {
        var user = userRepository.findByUsername(login.getUsername());
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!user.get().getPassword().equals(login.getPassword())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // create a cookie
        Cookie cookie = new Cookie("auth", login.getUsername());

        // expires in 7 days
        cookie.setMaxAge(7 * 24 * 60 * 60);

        // optional properties
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        // add cookie to response
        response.addCookie(cookie);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
