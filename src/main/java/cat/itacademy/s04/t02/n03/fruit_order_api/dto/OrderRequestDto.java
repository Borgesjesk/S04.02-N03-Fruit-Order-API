package cat.itacademy.s04.t02.n03.fruit_order_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    @NotBlank(message = "Client name cannot be empty")
    private String clientName;

    @Future(message = "Delivery date must be at least one day in the future")
    private LocalDate deliveryDate;

    @NotEmpty(message = "The order must contain at least one item")
    private List<@Valid OrderItemDto> items;
}