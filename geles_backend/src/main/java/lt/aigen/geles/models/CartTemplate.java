package lt.aigen.geles.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter
public class CartTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cartTemplate")
    private List<FlowerInCart> flowersInCart;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private String name;
}
