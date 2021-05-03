package lt.aigen.geles.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Getter @Setter
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    private String name;
    @NotBlank
    private String Address;
    @NotBlank
    private String information;

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    private List<Flower> offeredFlowers;
}
