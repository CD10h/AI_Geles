package lt.aigen.geles.models;

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
    private Long id;

    @NotBlank
    private String name;

    //@NotNull
    private Double price;

    //@NotNull
    private String description;

    private String photo;

    //@NotNull
    private Date expiryDate;

    public Flower(Long id, String name, Double price, String description, String photo, Date expiryDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.photo = photo;
        this.expiryDate = expiryDate;
    }

    public Flower() {

    }

    public Flower(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
