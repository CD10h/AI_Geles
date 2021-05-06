package lt.aigen.geles.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlowerInCartDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private Integer amount;
}
