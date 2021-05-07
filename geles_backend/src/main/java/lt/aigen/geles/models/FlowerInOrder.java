package lt.aigen.geles.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "FlowerInOrder")
public class FlowerInOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "flower_id")
    private Flower flower;

    @Transient
    public Double getTotalPrice() {
        return getFlower().getPrice() * getQuantity();
    }
}

