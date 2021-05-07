package lt.aigen.geles.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Orders")
public class Order implements Serializable {
    OrderStatus orderStatus;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String address;
    @NotNull
    private String contactPhone;
    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date createdDate;
    @OneToMany(mappedBy = "order")
    private List<FlowerInOrder> orderProducts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }

    @Transient
    public Double getTotalOrderPrice() {
        return getOrderProducts().stream()
                .map(FlowerInOrder::getTotalPrice)
                .reduce(0.0, Double::sum);
    }

    private enum OrderStatus {
        UNPAID,
        PAID,
        DELIVERED
    }
}
