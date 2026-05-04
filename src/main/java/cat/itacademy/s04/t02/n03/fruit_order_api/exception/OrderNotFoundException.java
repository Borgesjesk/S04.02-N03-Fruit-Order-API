package cat.itacademy.s04.t02.n03.fruit_order_api.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }
}
