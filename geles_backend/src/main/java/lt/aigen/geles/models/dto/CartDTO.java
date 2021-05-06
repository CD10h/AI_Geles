package lt.aigen.geles.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lt.aigen.geles.models.FlowerInCart;

import java.util.List;

@Getter
@Setter
public class CartDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private List<FlowerInCart> flowersInCart;
}
