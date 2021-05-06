package lt.aigen.geles.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter @Setter
public class Cart implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart")
    private List<FlowerInCart> flowersInCart;

    @OneToOne(mappedBy = "cart")
    private User user;

    public Cart(){

    }

    public Cart(User user){
        this.user = user;
    }
}
