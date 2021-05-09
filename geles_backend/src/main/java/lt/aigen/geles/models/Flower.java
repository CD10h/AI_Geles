package lt.aigen.geles.models;

import lombok.Getter;
import lombok.Setter;
import lt.aigen.geles.models.dto.FlowerDTO;
import org.hibernate.annotations.Type;

import javax.validation.constraints.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NamedNativeQuery(name = "Flower.findAllFlowersWithFavoriteWithQuery",
        query = "select f.id, f.name, f.price, f.description, f.days_to_expire, f.photo, bool_or(coalesce(u.username = :username, false)) as favorite from flower f left join user_flower uf on f.id = uf.flower_id left join users u on u.id = uf.user_id where lower(f.name) like lower(concat('%', :query, '%')) group by 1", resultSetMapping = "mapFlowersWithFavorite")
@NamedNativeQuery(name = "Flower.findAllFavoriteFlowersWithQuery",
        query = "select f.id, f.name, f.price, f.description, f.days_to_expire, f.photo, true as favorite from flower f left join user_flower uf on f.id = uf.flower_id left join users u on u.id = uf.user_id where u.username = :username and lower(f.name) like lower(concat('%', :query, '%'))", resultSetMapping = "mapFlowersWithFavorite")
@NamedNativeQuery(name = "Flower.findFlowerWithFavorite",
        query = "select f.id, f.name, f.price, f.description, f.days_to_expire, f.photo, bool_or(coalesce(uf.user_id = :userId, false)) as favorite from flower f left join user_flower uf on f.id = uf.flower_id where f.id = :flowerId group by 1", resultSetMapping = "mapFlowersWithFavorite")
@SqlResultSetMapping(name="mapFlowersWithFavorite", classes = {
    @ConstructorResult(
        targetClass = FlowerDTO.class,
        columns = {
            @ColumnResult(name="id", type=Long.class),
            @ColumnResult(name="name"),
            @ColumnResult(name="price"),
            @ColumnResult(name="description"),
            @ColumnResult(name="days_to_expire"),
            @ColumnResult(name="photo"),
            @ColumnResult(name="favorite")
        }
    )
})
public class Flower implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Min(0)
    private Double price;

    @NotNull
    @Type(type="text")
    private String description;

    @Type(type="text")
    private String photo;

    @NotNull
    @Min(1)
    private Integer daysToExpire;

    @ManyToMany
    @JoinTable(
        name = "user_flower",
        joinColumns = @JoinColumn(name = "flower_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "flower_id"})
    )
    private Set<User> userFavorites = new HashSet<>();

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
}
