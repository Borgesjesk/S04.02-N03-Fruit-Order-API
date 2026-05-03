package cat.itacademy.s04.t02.n03.fruit_order_api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    private String fruitName;
    private int quantityInKilos;

    public OrderItem(String fruitName, int quantityInKilos) {
        this.fruitName = fruitName;
        this.quantityInKilos = quantityInKilos;
    }
}