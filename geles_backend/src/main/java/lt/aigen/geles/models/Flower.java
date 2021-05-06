package lt.aigen.geles.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.validation.constraints.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Flower implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Min(0)
    private Double price;

    @NotNull
    @Type(type="text")
    private String description;

    @Type(type="text")
    private String photo;

    @NotNull
    @Min(1)
    private Integer daysToExpire;

    @ManyToMany(mappedBy = "favoriteFlowers")
    private List<User> userFavorites = new ArrayList<>();

    public Flower(Long id, String name, Double price, String description, String photo, Integer daysToExpire) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.photo = photo;
        this.daysToExpire = daysToExpire;
    }

    public Flower() {

    }

    public Flower(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
