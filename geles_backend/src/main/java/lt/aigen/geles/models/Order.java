package lt.aigen.geles.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter @Setter
@Table(name = "Orders")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String address;
    @NotNull
    private String contactPhone;

    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date createdDate;

    OrderStatus orderStatus;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="USER_ID")
    private User user;

    private enum OrderStatus {
        UNPAID,
        PAID,
        DELIVERED
    }

    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }
}
