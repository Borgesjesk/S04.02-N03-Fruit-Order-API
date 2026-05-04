package cat.itacademy.s04.t02.n03.fruit_order_api.dto;

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
public class OrderResponseDto {

    private String id;
    private String clientName;
    private LocalDate deliveryDate;
    private List<OrderItemDto> items;
    }