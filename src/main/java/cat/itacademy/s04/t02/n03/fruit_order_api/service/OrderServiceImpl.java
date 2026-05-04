package cat.itacademy.s04.t02.n03.fruit_order_api.service;

import cat.itacademy.s04.t02.n03.fruit_order_api.dto.OrderRequestDto;
import cat.itacademy.s04.t02.n03.fruit_order_api.dto.OrderResponseDto;
import cat.itacademy.s04.t02.n03.fruit_order_api.exception.OrderNotFoundException;
import cat.itacademy.s04.t02.n03.fruit_order_api.mapper.OrderMapper;
import cat.itacademy.s04.t02.n03.fruit_order_api.model.Order;
import cat.itacademy.s04.t02.n03.fruit_order_api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        Order order = OrderMapper.toEntity(orderRequestDto);
        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toResponseDto(savedOrder);
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto getOrderById(String id) {
        return orderRepository.findById(id)
                .map(OrderMapper::toResponseDto)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: %s".formatted(id)));
    }

    @Override
    public OrderResponseDto updateOrder(String id, OrderRequestDto orderRequestDto) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Cannot update. Order not found with id: %s".formatted(id));
        }
        Order orderToUpdate = OrderMapper.toEntity(orderRequestDto);
        orderToUpdate.setId(id);
        return OrderMapper.toResponseDto(orderRepository.save(orderToUpdate));
    }

    @Override
    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Cannot delete. Order not found with id: %s".formatted(id));
        }
        orderRepository.deleteById(id);
    }
}