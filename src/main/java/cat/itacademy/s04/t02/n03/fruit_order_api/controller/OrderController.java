package cat.itacademy.s04.t02.n03.fruit_order_api.controller;

import cat.itacademy.s04.t02.n03.fruit_order_api.dto.OrderRequestDto;
import cat.itacademy.s04.t02.n03.fruit_order_api.dto.OrderResponseDto;
import cat.itacademy.s04.t02.n03.fruit_order_api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequestDto) {
        log.info("POST /api/v1/orders - Creating new order for client: {}", orderRequestDto.getClientName());
        OrderResponseDto response = orderService.createOrder(orderRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable String id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable String id, @Valid @RequestBody OrderRequestDto orderRequestDto) {
        log.info("PUT /api/v1/orders/{} -  Updating order", id);
        return ResponseEntity.ok(orderService.updateOrder(id, orderRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        log.info("DELETE /api/v1/orders/{} -  Deleting order", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}