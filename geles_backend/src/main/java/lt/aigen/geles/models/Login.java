package lt.aigen.geles.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class Login {
    @NotBlank
    @Min(4)
    private String username;

    @NotBlank
    @Min(8)
    private String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Login() {

    }
}
