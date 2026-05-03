package cat.itacademy.s04.t02.n03.fruit_order_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    @NotBlank(message = "Fruit name cannot be empty")
    private String fruitName;

    @NotNull(message = "Quantity is requited")
    @Positive(message = "Quantity must be at least 1 kilo")
    private Integer quantityInKilos;
}