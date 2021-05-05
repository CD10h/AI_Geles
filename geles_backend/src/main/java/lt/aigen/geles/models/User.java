package lt.aigen.geles.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.validation.constraints.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users", uniqueConstraints =
@UniqueConstraint(name = "UNIQUE_USERNAME", columnNames = {"username"})
)
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 4, max=32)
    private String username;

    @NotBlank
    @Size(min = 8, max=64)
    private String password;

    // Profile picture
    @Type(type = "text")
    private String photo;

    @ManyToMany
    @JoinTable(
            name = "user_flower",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "flower_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "flower_id"})
    )
    private List<Flower> favoriteFlowers = new ArrayList<>();

    public User(Long id, String username, String password, String photo) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.photo = photo;
    }

    public User() {

    }
}
