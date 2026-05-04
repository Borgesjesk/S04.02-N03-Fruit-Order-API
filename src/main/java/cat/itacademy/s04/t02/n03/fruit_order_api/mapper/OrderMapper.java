package cat.itacademy.s04.t02.n03.fruit_order_api.mapper;

import cat.itacademy.s04.t02.n03.fruit_order_api.dto.OrderItemDto;
import cat.itacademy.s04.t02.n03.fruit_order_api.dto.OrderRequestDto;
import cat.itacademy.s04.t02.n03.fruit_order_api.dto.OrderResponseDto;
import cat.itacademy.s04.t02.n03.fruit_order_api.model.Order;
import cat.itacademy.s04.t02.n03.fruit_order_api.model.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    private OrderMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Order toEntity(OrderRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("OrderRequestDto cannot be null");
        }
        return new Order(
                dto.getClientName(),
                dto.getDeliveryDate(),
                mapDtoItemsToEntity(dto.getItems()));
    }

    public static OrderResponseDto toResponseDto(Order entity) {
        return new OrderResponseDto(
                entity.getId(),
                entity.getClientName(),
                entity.getDeliveryDate(),
                mapEntityItemsToDto(entity.getItems()));
    }

    public static List<OrderItem> mapDtoItemsToEntity(List<OrderItemDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream()
                .map(dto -> new OrderItem(dto.getFruitName(), dto.getQuantityInKilos()))
                .collect(Collectors.toList());
    }

    public static List<OrderItemDto> mapEntityItemsToDto(List<OrderItem> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(entity -> new OrderItemDto(entity.getFruitName(), entity.getQuantityInKilos()))
                .collect(Collectors.toList());
    }
}