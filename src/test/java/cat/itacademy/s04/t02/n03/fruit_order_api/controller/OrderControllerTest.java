package cat.itacademy.s04.t02.n03.fruit_order_api.controller;

import cat.itacademy.s04.t02.n03.fruit_order_api.dto.OrderItemDto;
import cat.itacademy.s04.t02.n03.fruit_order_api.dto.OrderRequestDto;
import cat.itacademy.s04.t02.n03.fruit_order_api.dto.OrderResponseDto;
import cat.itacademy.s04.t02.n03.fruit_order_api.exception.OrderNotFoundException;
import cat.itacademy.s04.t02.n03.fruit_order_api.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private static final String BASE_URL = "/api/v1/orders";
    private static final LocalDate FUTURE_DATE = LocalDate.now().plusDays(3);

    private OrderRequestDto validRequest() {
        return new OrderRequestDto(
                "Jess",
                FUTURE_DATE,
                List.of(new OrderItemDto("Mango", 4)));
    }

    private OrderResponseDto validResponse() {
        return new OrderResponseDto(
                "ABC123",
                "Jess",
                FUTURE_DATE,
                List.of(new OrderItemDto("Mango", 4)));
    }

    @Nested
    @DisplayName("POST /api/v1/orders")
    class CreateOrder {

        @Test
        @DisplayName("Returns 201 when data is valid")
        void shouldReturn201WhenValid() throws Exception {
            when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(validResponse());

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value("ABC123"))
                    .andExpect(jsonPath("$.clientName").value("Jess"))
                    .andExpect(jsonPath("$.deliveryDate").value(FUTURE_DATE.toString()))
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].fruitName").value("Mango"));

            verify(orderService).createOrder(any(OrderRequestDto.class));
        }

        @Test
        @DisplayName("Returns 400 when clientName is blank")
        void shouldReturn400WhenClientNameIsBlank() throws Exception {
            OrderRequestDto invalid = new OrderRequestDto(
                    "", FUTURE_DATE,
                    List.of(new OrderItemDto("Mango", 4)));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.clientName").exists());

            verify(orderService, never()).createOrder(any());
        }

        @Test
        @DisplayName("Returns 400 when deliveryDate is null")
        void shouldReturn400WhenDeliveryDateIsNull() throws Exception {
            OrderRequestDto invalid = new OrderRequestDto(
                    "Jess", null,
                    List.of(new OrderItemDto("Mango", 4)));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.deliveryDate").exists());

            verify(orderService, never()).createOrder(any());
        }

        @Test
        @DisplayName("Returns 400 when deliveryDate is in the past")
        void shouldReturn400WhenDeliveryDateInThePast() throws Exception {
            OrderRequestDto invalid = new OrderRequestDto(
                    "Jess", LocalDate.now().minusDays(1),
                    List.of(new OrderItemDto("Mango", 4)));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.deliveryDate").exists());

            verify(orderService, never()).createOrder(any());
        }

        @Test
        @DisplayName("Returns 400 when items list is empty")
        void shouldReturn400WhenItemsIsEmpty() throws Exception {

            OrderRequestDto invalid = new OrderRequestDto(
                    "Jess", FUTURE_DATE, Collections.emptyList());

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.items").exists());

            verify(orderService, never()).createOrder(any());
        }

        @Test
        @DisplayName("Returns 400 when quantity is null")
        void shouldReturn400WhenQuantityIsNull() throws Exception {

            OrderRequestDto invalid = new OrderRequestDto(
                    "Jess", FUTURE_DATE,
                    List.of(new OrderItemDto("Mango", null)));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).createOrder(any());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders")
    class GetAllOrders {

        @Test
        @DisplayName("Returns 200 with list of orders")
        void shouldReturn200WithListOfOrders() throws Exception {
            when(orderService.getAllOrders()).thenReturn(List.of(validResponse()));

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].clientName").value("Jess"));
        }

        @Test
        @DisplayName("Returns 200 with empty list when no orders")
        void shouldReturn200WithEmptyListWhenNoOrders() throws Exception {
            when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders/{id}")
    class GetOrderById {

        @Test
        @DisplayName("Returns 200 when order exists")
        void shouldReturn200WhenOrderExists() throws Exception {
            when(orderService.getOrderById("ABC123")).thenReturn(validResponse());

            mockMvc.perform(get(BASE_URL + "/ABC123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("ABC123"))
                    .andExpect(jsonPath("$.clientName").value("Jess"));
        }

        @Test
        @DisplayName("Returns 404 when order not found")
        void shouldReturn404WhenOrderNotFound() throws Exception {
            when(orderService.getOrderById("nonExistentOrderId"))
                    .thenThrow(new OrderNotFoundException("nonExistentOrderId"));

            mockMvc.perform(get(BASE_URL + "/nonExistentOrderId"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/orders/{id}")
    class UpdateOrder {

        @Test
        @DisplayName("Returns 200 when update is valid")
        void shouldReturn200WhenUpdateIsValid() throws Exception {
            when(orderService.updateOrder(eq("ABC123"),
                    any(OrderRequestDto.class))).thenReturn(validResponse());

            mockMvc.perform(put(BASE_URL + "/ABC123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("ABC123"));

            verify(orderService).updateOrder(eq("ABC123"), any(OrderRequestDto.class));
        }

        @Test
        @DisplayName("Returns 404 when order not found")
        void shouldReturn404WhenUpdateNotFound() throws Exception {
            when(orderService.updateOrder(eq("nonExistentOrderId"), any(OrderRequestDto.class)))
                    .thenThrow(new OrderNotFoundException("nonExistentOrderId"));

            mockMvc.perform(put(BASE_URL + "/nonExistentOrderId")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Returns 400 when update data is invalid")
        void shouldReturn400WhenUpdateDataIsInvalid() throws Exception {
            OrderRequestDto invalid = new OrderRequestDto(
                    "", null, Collections.emptyList());

            mockMvc.perform(put(BASE_URL + "/ABC123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).updateOrder(any(), any());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/orders/{id}")
    class DeleteOrder {

        @Test
        @DisplayName("Returns 204 when order is deleted")
        void shouldReturn204WhenOrderIsDeleted() throws Exception {
            doNothing().when(orderService).deleteOrder("ABC123");

            mockMvc.perform(delete(BASE_URL + "/ABC123"))
                    .andExpect(status().isNoContent());

            verify(orderService).deleteOrder("ABC123");
        }

        @Test
        @DisplayName("Returns 404 when order not found")
        void shouldReturn404WhenOrderNotFound() throws Exception {
            doThrow(new OrderNotFoundException("nonExistentOrderId"))
                    .when(orderService).deleteOrder("nonExistentOrderId");

            mockMvc.perform(delete(BASE_URL + "/nonExistentOrderId"))
                    .andExpect(status().isNotFound());
        }
    }
}