package lt.aigen.geles.models.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
public class VersionDTO {
    @NotNull
    Integer version;
}
