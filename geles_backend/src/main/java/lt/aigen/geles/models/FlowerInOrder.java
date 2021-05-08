package lt.aigen.geles.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "FlowerInOrder")
public class FlowerInOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer quantity;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "flower_id")
    private Flower flower;

    @Transient
    public Double getTotalPrice() {
        return getFlower().getPrice() * getQuantity();
    }
}

