package lt.aigen.geles.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter @Setter
public class Flower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    private String name;

    //@NotNull
    private Double price;

    //@NotNull
    private String description;

    private String photo;

    //@NotNull
    private Integer daysToExpire;

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
