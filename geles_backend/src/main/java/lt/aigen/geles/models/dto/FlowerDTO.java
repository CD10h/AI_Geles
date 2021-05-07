package lt.aigen.geles.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class FlowerDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Min(0)
    private Double price;

    @NotBlank
    private String description;

    @NotNull
    @Min(1)
    private Integer daysToExpire;

    private String photo;

    private boolean isFavorite;
}

