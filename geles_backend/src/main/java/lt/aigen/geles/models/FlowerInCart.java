package lt.aigen.geles.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter @Setter
public class FlowerInCart implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //join column
    @ManyToOne
    @JoinColumn(name = "flower_id")
    private Flower flower;

    //join column
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private Integer amount;

    public FlowerInCart(Flower fl, int am) {
        this.flower = fl;
        this.amount = am;
    }

    public FlowerInCart(){

    }
}
