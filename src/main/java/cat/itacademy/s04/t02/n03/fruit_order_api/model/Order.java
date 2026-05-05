package cat.itacademy.s04.t02.n03.fruit_order_api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String id;
    private String clientName;
    private LocalDate deliveryDate;
    private List<OrderItem> items;

    public Order(String clientName, LocalDate deliveryDate, List<OrderItem> items) {
        this.clientName = clientName;
        this.deliveryDate = deliveryDate;
        this.items = items;
    }
}