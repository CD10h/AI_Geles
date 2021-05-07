package lt.aigen.geles.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lt.aigen.geles.models.FlowerInOrder;
import lt.aigen.geles.models.User;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrderDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date createdDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double totalOrderPrice;


    @NotNull
    private String address;
    @NotNull
    private String contactPhone;



    private List<FlowerInOrderDTO> orderFlowers;
    @NotNull
    private Long userId;

    @NotNull
    private String orderStatus;
}
