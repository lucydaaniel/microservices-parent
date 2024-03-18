package com.schabikowski.OrderService.service;

import com.schabikowski.OrderService.dto.InventoryResponse;
import com.schabikowski.OrderService.dto.OrderLinesItemsDto;
import com.schabikowski.OrderService.dto.OrderRequest;
import com.schabikowski.OrderService.model.Order;
import com.schabikowski.OrderService.model.OrderLineItems;
import com.schabikowski.OrderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItems(orderRequest.getOrderLinesItemsDtoList().stream()
                        .map(this::mapFromDto)
                        .toList())
                .build();

        List<String> skuCodes = order.getOrderLineItems().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        InventoryResponse[] result = webClientBuilder
                .build()
                .get()
                .uri(
                        "http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(result).allMatch(InventoryResponse::getIsInStock);

        if (allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("There are not available product in stock!");
        }
    }

    private OrderLineItems mapFromDto(OrderLinesItemsDto orderLinesItemsDto) {
        return OrderLineItems.builder()
                .price(orderLinesItemsDto.getPrice())
                .quantity(orderLinesItemsDto.getQuantity())
                .skuCode(orderLinesItemsDto.getSkuCode())
                .build();
    }
}
