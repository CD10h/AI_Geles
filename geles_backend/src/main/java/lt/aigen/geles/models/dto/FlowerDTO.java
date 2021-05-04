package lt.aigen.geles.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class FlowerDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    private Long sellerId;
    @NotBlank
    private String name;

    private Double price;

    private String description;

    private String photo;

}

